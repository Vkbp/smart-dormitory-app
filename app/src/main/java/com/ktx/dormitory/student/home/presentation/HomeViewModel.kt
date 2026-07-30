package com.ktx.dormitory.student.home.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.auth.domain.model.UserData
import com.ktx.dormitory.shared.auth.domain.usecase.GetAuthStateUseCase
import com.ktx.dormitory.shared.profile.domain.usecase.GetProfileUseCase
import com.ktx.dormitory.student.room.domain.usecase.GetRoomInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getRoomInfoUseCase: GetRoomInfoUseCase
) : BaseViewModel<HomeContract.State, HomeContract.Event, HomeViewModel.Effect>(
    HomeContract.State()
) {

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            // Chỉ reset isLoading và error, giữ nguyên trạng thái isResident cũ để tránh nháy màn hình
            updateState { it.copy(
                isLoading = true, 
                error = null, 
                roomInfo = null
            ) }
            
            // 1. Lấy thông tin Auth cơ bản
            val authResult = getAuthStateUseCase()
            authResult.onSuccess { userData ->
                updateState { it.copy(userData = userData) }
            }

            // 2. Lấy thông tin Profile và thông tin Phòng song song
            val profileResult = getProfileUseCase()
            val roomResult = getRoomInfoUseCase()

            profileResult.onSuccess { profile ->
                val isResidentStatus = profile.status?.uppercase() != "INACTIVE" && 
                                     profile.status?.uppercase() != "CHECKED_OUT"
                
                val roomInfo = roomResult.getOrNull()
                val hasRoom = roomResult.isSuccess && roomInfo?.roomCode != null

                updateState { state ->
                    state.copy(
                        userData = state.userData?.copy(fullName = profile.fullName) ?: userDataWithProfile(profile.fullName),
                        roomInfo = roomInfo,
                        // Một người là cư dân nếu: Hồ sơ ACTIVE VÀ (Có phòng HOẶC chưa xác định rõ)
                        // Chỉ ẩn nút nếu trạng thái chắc chắn là INACTIVE/CHECKED_OUT
                        isResident = isResidentStatus,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun userDataWithProfile(fullName: String?): UserData {
        return UserData(username = "Unknown", fullName = fullName)
    }

    override fun onEvent(event: HomeContract.Event) {
        when (event) {
            HomeContract.Event.RefreshData -> loadHomeData()
        }
    }

    sealed interface Effect : com.ktx.dormitory.core.base.BaseContract.Effect
}
