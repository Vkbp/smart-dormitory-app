package com.ktx.dormitory.admin.extension.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.admin.extension.domain.usecase.GetStayExtensionsUseCase
import com.ktx.dormitory.admin.extension.domain.usecase.ReviewStayExtensionUseCase
import com.ktx.dormitory.admin.extension.domain.usecase.GetDetailedStudentProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StayExtensionViewModel @Inject constructor(
    private val getExtensionsUseCase: GetStayExtensionsUseCase,
    private val reviewUseCase: ReviewStayExtensionUseCase,
    private val getStudentProfileUseCase: GetDetailedStudentProfileUseCase
) : BaseViewModel<StayExtensionUiState, StayExtensionUiEvent, StayExtensionUiEffect>(StayExtensionUiState()) {

    init {
        onEvent(StayExtensionUiEvent.LoadExtensions(refresh = true))
    }

    override fun onEvent(event: StayExtensionUiEvent) {
        when (event) {
            is StayExtensionUiEvent.LoadExtensions -> loadExtensions(event.refresh, event.status)
            is StayExtensionUiEvent.ReviewExtension -> review(event.id, event.status, event.reason)
            is StayExtensionUiEvent.LoadStudentProfile -> loadProfile(event.studentId)
            StayExtensionUiEvent.ClearProfile -> updateState { it.copy(selectedStudentProfile = null) }
        }
    }

    private fun loadExtensions(refresh: Boolean, status: String?) {
        viewModelScope.launch {
            if (refresh) updateState { it.copy(isLoading = true, extensions = emptyList(), currentPage = 0, isLastPage = false, error = null) }
            
            val page = if (refresh) 0 else currentState.currentPage + 1
            getExtensionsUseCase(status, page, 15).onSuccess { response ->
                updateState { state ->
                    val rawContent = response.content ?: emptyList()
                    // Lọc tại Client vì API Backend có thể không hỗ trợ filter status
                    val filteredContent = if (status == "PENDING") {
                        rawContent.filter { it.status.uppercase() == "PENDING" }
                    } else rawContent
                    
                    val newList = if (refresh) filteredContent else state.extensions + filteredContent
                    state.copy(
                        extensions = newList,
                        currentPage = page,
                        isLastPage = page >= response.totalPages - 1,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun review(id: UUID, status: String, reason: String?) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            reviewUseCase(id, status, reason).onSuccess {
                sendEffect(StayExtensionUiEffect.ShowToast("Đã xử lý yêu cầu"))
                // Filter out the processed item immediately for better UX
                updateState { state ->
                    state.copy(extensions = state.extensions.filter { it.id != id })
                }
                loadExtensions(refresh = true, status = "PENDING")
            }.onFailure { e ->
                val errorMsg = e.message ?: "Lỗi xử lý"
                if (errorMsg.contains("đã được xử lý", ignoreCase = true)) {
                    // Nếu đơn đã xử lý rồi thì coi như thành công, xóa khỏi list local
                    sendEffect(StayExtensionUiEffect.ShowToast(errorMsg))
                    updateState { state ->
                        state.copy(
                            extensions = state.extensions.filter { it.id != id },
                            isLoading = false
                        )
                    }
                    loadExtensions(refresh = true, status = "PENDING")
                } else {
                    updateState { it.copy(error = errorMsg, isLoading = false) }
                }
            }
        }
    }

    private fun loadProfile(studentId: UUID) {
        viewModelScope.launch {
            updateState { it.copy(isLoadingProfile = true) }
            getStudentProfileUseCase(studentId)
                .onSuccess { profile ->
                    updateState { it.copy(selectedStudentProfile = profile, isLoadingProfile = false) }
                }
                .onFailure { e ->
                    updateState { it.copy(error = e.message, isLoadingProfile = false) }
                }
        }
    }
}
