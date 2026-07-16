package com.ktx.dormitory.student.face.presentation

import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto
import com.ktx.dormitory.student.face.data.dto.response.VerificationAttemptDto

data class FaceManagementUiState(
    val isLoading: Boolean = false,
    val faceProfile: FaceProfileDto? = null,
    val verifications: List<VerificationAttemptDto> = emptyList(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false,
    val currentPage: Int = 0,
    val isLastPage: Boolean = false
)

sealed class FaceManagementUiEvent {
    data object Refresh : FaceManagementUiEvent()
    data object LoadMoreVerifications : FaceManagementUiEvent()
    data class RequestReplacement(val imagePath: String) : FaceManagementUiEvent()
    data object ClearError : FaceManagementUiEvent()
}
