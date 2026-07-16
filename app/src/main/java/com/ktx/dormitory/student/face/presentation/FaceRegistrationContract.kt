package com.ktx.dormitory.student.face.presentation

import com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto

data class FaceRegistrationUiState(
    val isLoading: Boolean = false,
    val isRegistering: Boolean = false,
    val faceProfile: FaceProfileDto? = null,
    val errorMessage: String? = null,
    val registrationSuccess: Boolean = false,
    val facePositionOk: Boolean = false,
)

sealed class FaceRegistrationUiEvent {
    data object LoadProfile : FaceRegistrationUiEvent()
    data class RegisterFace(val name: String, val imagePath: String) : FaceRegistrationUiEvent()
    data class RequestReplacement(val imagePath: String) : FaceRegistrationUiEvent()
    data object ResetStatus : FaceRegistrationUiEvent()
    data object ClearError : FaceRegistrationUiEvent()
}

sealed class FaceRegistrationUiEffect {
    data object NavigateBack : FaceRegistrationUiEffect()
    data class ShowToast(val message: String) : FaceRegistrationUiEffect()
}
