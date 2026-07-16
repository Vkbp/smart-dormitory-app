package com.ktx.dormitory.admin.face.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.admin.common.domain.usecase.ApproveFaceUseCase
import com.ktx.dormitory.admin.common.domain.usecase.GetPendingFaceProfilesUseCase
import com.ktx.dormitory.admin.common.domain.usecase.RejectFaceUseCase
import com.ktx.dormitory.admin.common.data.mapper.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FaceApprovalViewModel @Inject constructor(
    private val getPendingFaceProfilesUseCase: GetPendingFaceProfilesUseCase,
    private val approveFaceUseCase: ApproveFaceUseCase,
    private val rejectFaceUseCase: RejectFaceUseCase
) : BaseViewModel<FaceApprovalUiState, FaceApprovalUiEvent, FaceApprovalUiEffect>(FaceApprovalUiState()) {

    init {
        onEvent(FaceApprovalUiEvent.LoadPendingProfiles)
    }

    override fun onEvent(event: FaceApprovalUiEvent) {
        when (event) {
            FaceApprovalUiEvent.LoadPendingProfiles -> loadPending()
            is FaceApprovalUiEvent.ApproveProfile -> approve(event.profileId)
            is FaceApprovalUiEvent.RejectProfile -> reject(event.profileId, event.reason)
        }
    }

    private fun loadPending() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            try {
                getPendingFaceProfilesUseCase(0, 20)
                    .onSuccess { page ->
                        updateState { it.copy(
                            pendingProfiles = page.content?.map { it.toDomain() } ?: emptyList()
                        ) }
                    }
                    .onFailure { error ->
                        updateState { it.copy(errorMessage = error.message) }
                    }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = "Lỗi tải dữ liệu: ${e.message}") }
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private fun approve(profileId: UUID) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            try {
                approveFaceUseCase(profileId)
                    .onSuccess { message ->
                        updateState { it.copy(successMessage = message) }
                        loadPending()
                    }
                    .onFailure { error ->
                        updateState { it.copy(errorMessage = error.message) }
                    }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = "Lỗi phê duyệt: ${e.message}") }
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private fun reject(profileId: UUID, reason: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            try {
                rejectFaceUseCase(profileId, reason)
                    .onSuccess { message ->
                        updateState { it.copy(successMessage = message) }
                        loadPending()
                    }
                    .onFailure { error ->
                        updateState { it.copy(errorMessage = error.message) }
                    }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = "Lỗi từ chối: ${e.message}") }
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }
}
