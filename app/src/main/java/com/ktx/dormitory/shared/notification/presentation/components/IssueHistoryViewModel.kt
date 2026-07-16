package com.ktx.dormitory.shared.notification.presentation.components

import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.shared.notification.domain.usecase.GetIssueHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IssueHistoryViewModel @Inject constructor(
    private val getIssueHistoryUseCase: GetIssueHistoryUseCase
) : BaseViewModel<IssueHistoryUiState, IssueHistoryUiEvent, IssueHistoryUiEffect>(IssueHistoryUiState()) {

    init {
        loadHistory()
    }

    override fun onEvent(event: IssueHistoryUiEvent) {
        when (event) {
            IssueHistoryUiEvent.Refresh -> loadHistory()
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, error = null) }
            getIssueHistoryUseCase()
                .onSuccess { issues ->
                    updateState { it.copy(isLoading = false, issues = issues) }
                }
                .onFailure { error ->
                    updateState { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}
