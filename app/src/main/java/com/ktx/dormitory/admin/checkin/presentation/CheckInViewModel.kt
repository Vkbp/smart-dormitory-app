package com.ktx.dormitory.admin.checkin.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.admin.checkin.domain.usecase.AssignRfidUseCase
import com.ktx.dormitory.admin.checkin.domain.usecase.ConfirmCheckInUseCase
import com.ktx.dormitory.admin.checkin.domain.usecase.SearchStudentForCheckInUseCase
import com.ktx.dormitory.admin.common.data.mapper.toDomain
import com.ktx.dormitory.core.network.toUserFriendlyMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import java.util.UUID
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val searchStudentUseCase: SearchStudentForCheckInUseCase,
    private val confirmCheckInUseCase: ConfirmCheckInUseCase,
    private val assignRfidUseCase: AssignRfidUseCase
) : BaseViewModel<CheckInUiState, CheckInUiEvent, CheckInUiEffect>(CheckInUiState()) {

    private var searchJob: Job? = null

    override fun onEvent(event: CheckInUiEvent) {
        when (event) {
            is CheckInUiEvent.SearchStudent -> search(event.cccd)
            is CheckInUiEvent.ConfirmCheckIn -> confirm(event.assignmentId)
            is CheckInUiEvent.AssignRfid -> assignRfid(event.studentId, event.rfidCode)
            CheckInUiEvent.ClearStatus -> updateState { it.copy(successMessage = null, errorMessage = null, studentInfo = null) }
        }
    }

    private fun search(cccd: String) {
        // Tránh search các chuỗi quá ngắn hoặc rỗng
        if (cccd.length < 9) return

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                // Thêm debounce nhẹ (300ms) để tránh spam API khi người dùng đang nhập
                delay(300)

                updateState { it.copy(isLoading = true, studentInfo = null, errorMessage = null) }
                
                val startTime = System.currentTimeMillis()
                Timber.d("API Search Request started for CCCD: $cccd")

                searchStudentUseCase(cccd)
                    .onSuccess { student ->
                        val endTime = System.currentTimeMillis()
                        Timber.d("API Search Response received in ${endTime - startTime}ms")
                        updateState { it.copy(studentInfo = student.toDomain()) }
                    }
                    .onFailure { error ->
                        val endTime = System.currentTimeMillis()
                        val errorMessage = error.toUserFriendlyMessage()
                        Timber.w(error, "API Search Failed after ${endTime - startTime}ms: $errorMessage")
                        updateState { it.copy(errorMessage = errorMessage) }
                    }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    Timber.e(e, "Unexpected error in search: ${e.message}")
                    updateState { it.copy(errorMessage = "Lỗi hệ thống: ${e.message}") }
                }
            } finally {
                // Đảm bảo tắt loading trừ khi job bị cancel bởi request mới
                if (isActive) {
                    updateState { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun confirm(assignmentId: UUID) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                confirmCheckInUseCase(assignmentId)
                    .onSuccess { message ->
                        updateState { it.copy(successMessage = message, studentInfo = null) }
                        // Bắn Effect để màn hình biết là thành công (có thể dùng để phát tiếng Bíp)
                        sendEffect(CheckInUiEffect.ShowToast(message))
                    }
                    .onFailure { error ->
                        updateState { it.copy(errorMessage = error.message) }
                    }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = "Lỗi xác nhận: ${e.message}") }
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    private fun assignRfid(studentId: UUID, rfidCode: String) {
        if (rfidCode.isBlank()) {
            updateState { it.copy(errorMessage = "Mã thẻ RFID không được để trống") }
            return
        }

        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = null) }
            try {
                assignRfidUseCase(studentId, rfidCode)
                    .onSuccess { message ->
                        sendEffect(CheckInUiEffect.ShowToast(message))
                    }
                    .onFailure { error ->
                        updateState { it.copy(errorMessage = error.toUserFriendlyMessage()) }
                    }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = "Lỗi gán thẻ: ${e.message}") }
            } finally {
                updateState { it.copy(isLoading = false) }
            }
        }
    }
}
