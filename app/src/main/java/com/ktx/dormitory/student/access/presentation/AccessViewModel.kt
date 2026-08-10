package com.ktx.dormitory.student.access.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ktx.dormitory.student.access.domain.model.UnifiedTimelineEvent
import com.ktx.dormitory.student.access.domain.model.CurfewRequestType
import com.ktx.dormitory.student.access.domain.usecase.*
import com.ktx.dormitory.shared.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccessViewModel @Inject constructor(
    private val getUnifiedAccessHistoryUseCase: GetUnifiedAccessHistoryUseCase,
    private val getAccessHistoryPagingUseCase: GetAccessHistoryPagingUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getCurfewRequestsUseCase: GetCurfewRequestsUseCase,
    private val submitCurfewRequestUseCase: SubmitCurfewRequestUseCase,
    private val observeCurfewRequestsUseCase: ObserveCurfewRequestsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccessUiState())
    val uiState: StateFlow<AccessUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AccessUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private val _accessPagingFlow = MutableStateFlow<PagingData<UnifiedTimelineEvent>>(PagingData.empty())
    val accessPagingFlow: Flow<PagingData<UnifiedTimelineEvent>> = _accessPagingFlow.asStateFlow()

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
            is AccessUiEvent.SubmitCurfewRequest -> {
                submitCurfewRequest(
                    event.requestType,
                    event.reason,
                    event.startDate,
                    event.expectedArrivalTime,
                    event.note
                )
            }
            is AccessUiEvent.ClearMessage -> _uiState.update { it.copy(uiMessage = null) }
            is AccessUiEvent.ResetSubmitSuccess -> _uiState.update { it.copy(submitSuccess = false) }
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

    private fun submitCurfewRequest(
        requestType: CurfewRequestType,
        reason: String,
        startDate: String?,
        expectedArrivalTime: String,
        note: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            submitCurfewRequestUseCase(requestType, reason, startDate, expectedArrivalTime, note).fold(
                onSuccess = { _ ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            submitSuccess = true,
                            uiMessage = "Gửi yêu cầu thành công"
                        ) 
                    }
                    _uiEffect.emit(AccessUiEffect.ShowToast("Gửi yêu cầu thành công"))
                    _uiEffect.emit(AccessUiEffect.NavigateBack)
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                    _uiEffect.emit(AccessUiEffect.ShowToast(e.message ?: "Lỗi gửi yêu cầu"))
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
            val studentId = getProfileUseCase().getOrNull()?.id ?: ""

            getUnifiedAccessHistoryUseCase(studentId, page = pageToLoad, size = 15).fold(
                onSuccess = { response ->
                    _uiState.update { state ->
                        val newList = if (refresh) response.content ?: emptyList() else state.logs + (response.content ?: emptyList())
                        state.copy(
                            logs = newList,
                            currentPage = pageToLoad,
                            isLastPage = pageToLoad >= response.totalPages - 1,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message ?: "Lỗi không xác định khi tải dữ liệu", isLoading = false) }
                }
            )
        }
    }
}
