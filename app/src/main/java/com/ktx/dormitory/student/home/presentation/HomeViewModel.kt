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
            updateState { it.copy(isLoading = true, error = null) }
            
            // Lấy thông tin Auth cơ bản
            val authResult = getAuthStateUseCase()
            authResult.onSuccess { userData ->
                updateState { it.copy(userData = userData) }
            }

            // Lấy thông tin Profile chi tiết để hiển thị tên thật
            val profileResult = getProfileUseCase()
            profileResult.onSuccess { profile ->
                updateState { state ->
                    state.copy(
                        userData = state.userData?.copy(fullName = profile.fullName) ?: userDataWithProfile(profile.fullName)
                    )
                }
            }
            
            // Lấy thông tin phòng
            val roomResult = getRoomInfoUseCase()
            roomResult.onSuccess { roomInfo ->
                updateState { it.copy(roomInfo = roomInfo, isLoading = false) }
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
