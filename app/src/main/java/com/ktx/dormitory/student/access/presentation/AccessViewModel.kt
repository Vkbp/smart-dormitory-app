package com.ktx.dormitory.student.access.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ktx.dormitory.student.access.domain.model.UnifiedEventType
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.data.dto.response.AccessLogDto
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto
import com.ktx.dormitory.student.access.domain.usecase.*
import com.ktx.dormitory.student.face.domain.usecase.GetFaceVerificationsUseCase
import com.ktx.dormitory.shared.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class AccessViewModel @Inject constructor(
    private val getAccessHistoryUseCase: GetAccessHistoryUseCase,
    private val getAccessHistoryPagingUseCase: GetAccessHistoryPagingUseCase,
    private val getFaceVerificationsUseCase: GetFaceVerificationsUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getCurfewRequestsUseCase: GetCurfewRequestsUseCase,
    private val submitCurfewRequestUseCase: SubmitCurfewRequestUseCase,
    private val observeCurfewRequestsUseCase: ObserveCurfewRequestsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccessUiState())
    val uiState: StateFlow<AccessUiState> = _uiState.asStateFlow()

    private val _accessPagingFlow = MutableStateFlow<PagingData<UnifiedTimelineEvent>>(PagingData.empty())
    val accessPagingFlow: Flow<PagingData<UnifiedTimelineEvent>> = _accessPagingFlow.asStateFlow()

    private val isoDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    init {
        observeCurfewRequests()
        initPaging()
        fetchCurfewRequests()
    }

    private fun initPaging() {
        viewModelScope.launch {
            val studentId = getProfileUseCase().getOrNull()?.id ?: ""
            getAccessHistoryPagingUseCase(studentId)
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _accessPagingFlow.value = pagingData
                }
        }
    }

    private fun observeCurfewRequests() {
        observeCurfewRequestsUseCase()
            .onEach { requests ->
                _uiState.update { it.copy(curfewRequests = requests) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: AccessUiEvent) {
        when (event) {
            is AccessUiEvent.FetchHistory -> fetchAccessHistory(refresh = true)
            is AccessUiEvent.FetchCurfewRequests -> fetchCurfewRequests()
            is AccessUiEvent.SubmitCurfewRequest -> submitCurfewRequest(event.reason, event.expectedArrivalTime, event.note)
            is AccessUiEvent.ClearMessage -> _uiState.update { it.copy(uiMessage = null) }
            else -> {}
        }
    }

    fun fetchCurfewRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getCurfewRequestsUseCase().fold(
                onSuccess = { _ ->
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
            )
        }
    }

    private fun submitCurfewRequest(reason: String, expectedArrivalTime: String, note: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            submitCurfewRequestUseCase(reason, expectedArrivalTime, note).fold(
                onSuccess = { _ ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            uiMessage = "Gửi yêu cầu thành công"
                        ) 
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
            )
        }
    }

    fun fetchAccessHistory(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _uiState.update { it.copy(isLoading = true, error = null, logs = emptyList(), currentPage = 0, isLastPage = false) }
            } else if (_uiState.value.isLastPage || _uiState.value.isLoading) {
                return@launch
            }

            val pageToLoad = if (refresh) 0 else _uiState.value.currentPage + 1
            
            // Get studentId from local profile
            val studentId = getProfileUseCase().getOrNull()?.id ?: ""

            try {
                coroutineScope {
                    // Call both APIs concurrently as per requirements
                    val accessDeferred = async { getAccessHistoryUseCase(page = pageToLoad, size = 15) }
                    val verificationDeferred = async { getFaceVerificationsUseCase(studentId, page = pageToLoad, size = 15) }

                    val accessResult = accessDeferred.await()
                    val verificationResult = verificationDeferred.await()

                    if (accessResult.isSuccess && verificationResult.isSuccess) {
                        val accessResponse = accessResult.getOrThrow()
                        val verificationResponse = verificationResult.getOrThrow()

                        // Unified Timeline Algorithm (O(N))
                        val merged = mergeEvents(
                            verificationResponse.content ?: emptyList(),
                            accessResponse.content ?: emptyList()
                        )

                        _uiState.update { state ->
                            val newList = if (refresh) merged else state.logs + merged
                            state.copy(
                                logs = newList,
                                currentPage = pageToLoad,
                                isLastPage = pageToLoad >= accessResponse.totalPages - 1,
                                isLoading = false
                            )
                        }
                    } else {
                        val error = accessResult.exceptionOrNull()?.message 
                            ?: verificationResult.exceptionOrNull()?.message 
                            ?: "Lỗi không xác định khi tải dữ liệu"
                        _uiState.update { it.copy(error = error, isLoading = false) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Đã có lỗi xảy ra", isLoading = false) }
            }
        }
    }

    /**
     * Thuật toán hợp nhất (Unified Timeline Algorithm)
     * Gom nhóm các sự kiện Verification và Access lệch nhau < 5 giây.
     */
    private fun mergeEvents(
        verifications: List<VerificationAttemptDto>,
        accessLogs: List<AccessLogDto>
    ): List<UnifiedTimelineEvent> {
        val unifiedEvents = mutableListOf<UnifiedTimelineEvent>()
        val usedAccessIds = mutableSetOf<String>()

        // 1. Duyệt qua các bản ghi Verification
        for (v in verifications) {
            val vTime = parseTimestamp(v.attemptedAt)
            
            // Tìm bản ghi Access tương ứng trong khoảng 10 giây (tăng độ trễ cho IoT)
            val matchingAccess = accessLogs.find { a ->
                val aTime = parseTimestamp(a.eventTimestamp ?: "")
                abs(vTime - aTime) < 10000 && a.id !in usedAccessIds
            }

            if (matchingAccess != null) {
                usedAccessIds.add(matchingAccess.id)
                
                val type = when {
                    v.status == "SUCCESS" && matchingAccess.decision == "GRANTED" -> UnifiedEventType.SUCCESS
                    v.status == "SUCCESS" && matchingAccess.decision == "DENIED" -> UnifiedEventType.ACCESS_DENIED
                    else -> UnifiedEventType.UNKNOWN
                }

                unifiedEvents.add(
                    UnifiedTimelineEvent(
                        id = matchingAccess.id,
                        timestamp = matchingAccess.eventTimestamp ?: v.attemptedAt,
                        type = type,
                        gateId = matchingAccess.gateId,
                        buildingId = matchingAccess.buildingId,
                        method = matchingAccess.method,
                        confidenceScore = v.confidenceScore,
                        denialReason = mapDenialReason(matchingAccess.denialReason),
                        verificationStatus = v.status,
                        accessDecision = matchingAccess.decision
                    )
                )
            } else {
                // TRƯỜNG HỢP KHÔNG TÌM THẤY MATCH (Do độ trễ hoặc lỗi cửa)
                unifiedEvents.add(
                    UnifiedTimelineEvent(
                        id = v.attemptId.toString(),
                        timestamp = v.attemptedAt,
                        // Nếu AI báo SUCCESS nhưng cửa chưa trả log -> Vẫn hiện XANH (hoặc UNKNOWN tùy bạn muốn)
                        // Ở đây tôi chọn SUCCESS để user biết AI đã nhận ra họ.
                        type = if (v.status == "SUCCESS") UnifiedEventType.SUCCESS else UnifiedEventType.VERIFY_FAIL,
                        gateId = v.gateDeviceId,
                        buildingId = null,
                        method = "FACE_AI",
                        confidenceScore = v.confidenceScore,
                        verificationStatus = v.status,
                        denialReason = if (v.status == "SUCCESS") "Đang xử lý mở cửa..." else null
                    )
                )
            }
        }

        // 2. Thêm các bản ghi Access không khớp với Face (VD: dùng Thẻ/QR)
        accessLogs.filter { it.id !in usedAccessIds }.forEach { a ->
            unifiedEvents.add(
                UnifiedTimelineEvent(
                    id = a.id,
                    timestamp = a.eventTimestamp ?: "",
                    type = if (a.decision == "GRANTED") UnifiedEventType.SUCCESS else UnifiedEventType.ACCESS_DENIED,
                    gateId = a.gateId,
                    buildingId = a.buildingId,
                    method = a.method,
                    denialReason = mapDenialReason(a.denialReason),
                    accessDecision = a.decision
                )
            )
        }

        return unifiedEvents.sortedByDescending { it.timestamp ?: "" }
    }

    private fun parseTimestamp(ts: String): Long {
        if (ts.isBlank()) return 0L
        return try {
            // Xử lý chuỗi ISO linh hoạt hơn (bỏ phần mili giây nếu có để parse chuẩn)
            val cleanTs = if (ts.contains(".")) ts.substringBefore(".") else if (ts.endsWith("Z")) ts.substringBefore("Z") else ts
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            format.parse(cleanTs)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    private fun mapDenialReason(reason: String?): String? {
        return when (reason) {
            "CURFEW_VIOLATION" -> "Vi phạm giờ giới nghiêm (Quá 23:00)"
            "OUTSIDE_TIME_WINDOW" -> "Chưa đến khung giờ được phép ra vào"
            "UNAUTHORIZED_OR_INACTIVE" -> "Tài khoản bị đình chỉ hoặc chưa kích hoạt"
            null -> null
            else -> reason
        }
    }
}

