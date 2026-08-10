package com.ktx.dormitory.student.face.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.student.face.domain.usecase.GetFaceProfileUseCase
import com.ktx.dormitory.student.face.domain.usecase.GetFaceVerificationsUseCase
import com.ktx.dormitory.student.face.domain.usecase.RequestFaceReplacementUseCase
import com.ktx.dormitory.shared.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FaceManagementViewModel @Inject constructor(
    private val getFaceProfileUseCase: GetFaceProfileUseCase,
    private val getFaceVerificationsUseCase: GetFaceVerificationsUseCase,
    private val requestFaceReplacementUseCase: RequestFaceReplacementUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FaceManagementUiState())
    val uiState: StateFlow<FaceManagementUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val profile = getProfileUseCase().getOrNull()
                val studentId = profile?.id
                
                if (studentId == null) {
                    _uiState.update { it.copy(errorMessage = "Không tìm thấy thông tin sinh viên") }
                    return@launch
                }

                // Load profile and first page of verifications
                fetchFaceProfile(studentId)
                fetchVerifications(studentId, 0, refresh = true)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi khởi tạo: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchFaceProfile(studentId: String) {
        getFaceProfileUseCase(studentId).onSuccess { profile ->
            _uiState.update { it.copy(faceProfile = profile) }
        }.onFailure { e ->
            _uiState.update { it.copy(errorMessage = "Lỗi tải hồ sơ: ${e.message}") }
        }
    }

    private suspend fun fetchVerifications(studentId: String, page: Int, refresh: Boolean) {
        getFaceVerificationsUseCase(studentId, page, 20).onSuccess { response ->
            _uiState.update { state ->
                val newList = if (refresh) (response.content ?: emptyList()) else state.verifications + (response.content ?: emptyList())
                state.copy(
                    verifications = newList,
                    currentPage = page,
                    isLastPage = page >= response.totalPages - 1,
                    isRefreshing = false
                )
            }
        }.onFailure { e ->
            _uiState.update { it.copy(errorMessage = "Lỗi tải lịch sử: ${e.message}", isRefreshing = false) }
        }
    }

    fun onEvent(event: FaceManagementUiEvent) {
        viewModelScope.launch {
            val profile = getProfileUseCase().getOrNull()
            val studentId = profile?.id ?: return@launch

            when (event) {
                is FaceManagementUiEvent.Refresh -> {
                    _uiState.update { it.copy(isRefreshing = true) }
                    fetchFaceProfile(studentId)
                    fetchVerifications(studentId, 0, refresh = true)
                }
                is FaceManagementUiEvent.LoadMoreVerifications -> {
                    if (!_uiState.value.isLastPage && !_uiState.value.isLoading) {
                        fetchVerifications(studentId, _uiState.value.currentPage + 1, refresh = false)
                    }
                }
                is FaceManagementUiEvent.RequestReplacement -> {
                    _uiState.update { it.copy(isLoading = true) }
                    try {
                        requestFaceReplacementUseCase(studentId, File(event.imagePath)).onSuccess {
                            fetchFaceProfile(studentId)
                        }.onFailure { e ->
                            _uiState.update { it.copy(errorMessage = "Yêu cầu thay đổi thất bại: ${e.message}") }
                        }
                    } catch (e: Exception) {
                        _uiState.update { it.copy(errorMessage = "Lỗi hệ thống: ${e.message}") }
                    } finally {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                }
                is FaceManagementUiEvent.ClearError -> {
                    _uiState.update { it.copy(errorMessage = null) }
                }
            }
        }
    }
}
