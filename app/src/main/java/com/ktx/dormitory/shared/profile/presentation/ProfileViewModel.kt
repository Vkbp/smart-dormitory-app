package com.ktx.dormitory.shared.profile.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.profile.domain.usecase.GetProfileUseCase
import com.ktx.dormitory.shared.profile.domain.usecase.UpdateProfileUseCase
import com.ktx.dormitory.shared.profile.domain.usecase.UploadAvatarUseCase
import com.ktx.dormitory.shared.auth.domain.usecase.LogoutUseCase
import com.ktx.dormitory.core.util.ValidationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    private val logoutUseCase: LogoutUseCase,
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
            is ProfileUiEvent.UpdateProfile -> updateProfile(
                event.phone,
                event.permanentAddress,
                event.emergencyContact,
                event.fatherName,
                event.fatherPhone,
                event.motherName,
                event.motherPhone
            )
            is ProfileUiEvent.UploadAvatar -> uploadAvatar(event.filePath)
            ProfileUiEvent.ClearUploadStatus -> updateState { it.copy(uploadSuccess = false) }
            ProfileUiEvent.Logout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
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

    private fun updateProfile(
        phone: String,
        permanentAddress: String,
        emergencyContact: String,
        fatherName: String,
        fatherPhone: String,
        motherName: String,
        motherPhone: String
    ) {
        viewModelScope.launch {
            var hasError = false
            if (!ValidationUtils.isValidPhone(phone)) {
                updateState { it.copy(phoneError = "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)") }
                hasError = true
            } else {
                updateState { it.copy(phoneError = null) }
            }

            if (hasError) return@launch

            updateState { it.copy(isLoading = true) }
            updateProfileUseCase(
                phone = phone,
                permanentAddress = permanentAddress,
                emergencyContact = emergencyContact,
                fatherName = fatherName,
                fatherPhone = fatherPhone,
                motherName = motherName,
                motherPhone = motherPhone,
                avatarUrl = currentState.profile?.avatarUrl // Giữ nguyên avatarUrl hiện tại khi cập nhật các thông tin khác
            ).onSuccess {
                updateState { state ->
                    state.copy(
                        profile = state.profile?.copy(
                            phone = phone,
                            permanentAddress = permanentAddress,
                            emergencyContact = emergencyContact,
                            fatherName = fatherName,
                            fatherPhone = fatherPhone,
                            motherName = motherName,
                            motherPhone = motherPhone
                        )
                    )
                }
                sendEffect(ProfileUiEffect.ShowToast("Cập nhật thành công"))
            }.onFailure { e ->
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
                    // Sau khi tải ảnh lên Cloudinary thành công, cần gọi API update profile để lưu URL này vào Database
                    updateProfileUseCase(avatarUrl = newUrl)
                        .onSuccess {
                            updateState { state ->
                                state.copy(
                                    uploadSuccess = true,
                                    profile = state.profile?.copy(avatarUrl = newUrl)
                                )
                            }
                            sendEffect(ProfileUiEffect.ShowToast("Cập nhật ảnh đại diện thành công"))
                        }
                        .onFailure { e ->
                            updateState { it.copy(error = "Lưu ảnh vào hồ sơ thất bại: ${e.message}") }
                        }
                }
                .onFailure { e ->
                    updateState { it.copy(error = e.message) }
                }
            updateState { it.copy(isUploading = false) }
        }
    }
}
