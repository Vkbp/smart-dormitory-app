package com.ktx.dormitory.shared.profile.presentation

import android.os.Parcelable
import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.shared.profile.domain.model.UserProfile
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val error: String? = null,
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false
) : BaseContract.State, Parcelable

sealed class ProfileUiEvent : BaseContract.Event {
    data object LoadProfile : ProfileUiEvent()
    data class UpdateProfile(val fullName: String, val phone: String, val email: String) : ProfileUiEvent()
    data class UploadAvatar(val filePath: String) : ProfileUiEvent()
    data object ClearUploadStatus : ProfileUiEvent()
    data object Logout : ProfileUiEvent()
}

sealed class ProfileUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : ProfileUiEffect()
}
