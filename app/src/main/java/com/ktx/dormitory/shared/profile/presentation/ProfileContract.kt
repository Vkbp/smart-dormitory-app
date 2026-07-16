package com.ktx.dormitory.shared.profile.presentation

import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.shared.profile.domain.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null,
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false
) : BaseContract.State

sealed class ProfileUiEvent : BaseContract.Event {
    data object LoadProfile : ProfileUiEvent()
    data class UpdateProfile(val fullName: String, val phone: String, val email: String) : ProfileUiEvent()
    data class UploadAvatar(val filePath: String) : ProfileUiEvent()
    data object ClearUploadStatus : ProfileUiEvent()
}

sealed class ProfileUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : ProfileUiEffect()
}
