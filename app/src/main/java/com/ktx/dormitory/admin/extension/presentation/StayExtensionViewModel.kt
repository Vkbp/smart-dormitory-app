package com.ktx.dormitory.admin.extension.presentation

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.admin.common.domain.usecase.GetStayExtensionsUseCase
import com.ktx.dormitory.admin.common.domain.usecase.ReviewStayExtensionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StayExtensionViewModel @Inject constructor(
    private val getExtensionsUseCase: GetStayExtensionsUseCase,
    private val reviewUseCase: ReviewStayExtensionUseCase
) : BaseViewModel<StayExtensionUiState, StayExtensionUiEvent, StayExtensionUiEffect>(StayExtensionUiState()) {

    init {
        onEvent(StayExtensionUiEvent.LoadExtensions(refresh = true))
    }

    override fun onEvent(event: StayExtensionUiEvent) {
        when (event) {
            is StayExtensionUiEvent.LoadExtensions -> loadExtensions(event.refresh)
            is StayExtensionUiEvent.ReviewExtension -> review(event.id, event.status, event.reason)
        }
    }

    private fun loadExtensions(refresh: Boolean) {
        viewModelScope.launch {
            if (refresh) updateState { it.copy(isLoading = true, extensions = emptyList(), currentPage = 0, isLastPage = false) }
            
            val page = if (refresh) 0 else currentState.currentPage + 1
            getExtensionsUseCase(page, 15).onSuccess { response ->
                updateState { state ->
                    val newList = if (refresh) (response.content ?: emptyList()) else state.extensions + (response.content ?: emptyList())
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
            updateState { it.copy(isLoading = true) }
            reviewUseCase(id, status, reason).onSuccess {
                sendEffect(StayExtensionUiEffect.ShowToast("Đã xử lý yêu cầu"))
                loadExtensions(refresh = true)
            }.onFailure { e ->
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
