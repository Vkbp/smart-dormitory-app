package com.ktx.dormitory.shared.profile.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.profile.domain.usecase.GetProfileUseCase
import com.ktx.dormitory.shared.profile.domain.usecase.UpdateProfileUseCase
import com.ktx.dormitory.shared.profile.domain.usecase.UploadAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<ProfileUiState, ProfileUiEvent, ProfileUiEffect>(
    savedStateHandle.get<ProfileUiState>("uiState") ?: ProfileUiState()
) {

    init {
        viewModelScope.launch {
            uiState.collect {
                savedStateHandle["uiState"] = it
            }
        }
        if (currentState.profile == null) {
            loadProfile()
        }
    }

    override fun onEvent(event: ProfileUiEvent) {
        when (event) {
            ProfileUiEvent.LoadProfile -> loadProfile()
            is ProfileUiEvent.UpdateProfile -> updateProfile(event.fullName, event.phone, event.email)
            is ProfileUiEvent.UploadAvatar -> uploadAvatar(event.filePath)
            ProfileUiEvent.ClearUploadStatus -> updateState { it.copy(uploadSuccess = false) }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            getProfileUseCase()
                .onSuccess { profile ->
                    updateState { it.copy(profile = profile) }
                }
                .onFailure { e ->
                    updateState { it.copy(error = e.message) }
                }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun updateProfile(fullName: String, phone: String, email: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            updateProfileUseCase(phone, email)
                .onSuccess {
                    updateState { state ->
                        state.copy(
                            profile = state.profile?.copy(
                                fullName = fullName,
                                phone = phone,
                                email = email
                            )
                        )
                    }
                    sendEffect(ProfileUiEffect.ShowToast("Cập nhật thành công"))
                }
                .onFailure { e ->
                    updateState { it.copy(error = e.message) }
                }
            updateState { it.copy(isLoading = false) }
        }
    }

    private fun uploadAvatar(filePath: String) {
        viewModelScope.launch {
            updateState { it.copy(isUploading = true, uploadSuccess = false) }
            uploadAvatarUseCase(filePath)
                .onSuccess { newUrl ->
                    updateState { state ->
                        state.copy(
                            uploadSuccess = true,
                            profile = state.profile?.copy(avatarUrl = newUrl)
                        )
                    }
                    sendEffect(ProfileUiEffect.ShowToast("Tải ảnh lên thành công"))
                }
                .onFailure { e ->
                    updateState { it.copy(error = e.message) }
                }
            updateState { it.copy(isUploading = false) }
        }
    }
}
