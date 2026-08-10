package com.ktx.dormitory.admin.smartaccess.presentation.history

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ktx.dormitory.core.base.BaseViewModel
import com.ktx.dormitory.admin.smartaccess.domain.usecase.GetAdminAccessHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdminAccessHistoryViewModel @Inject constructor(
    private val getAdminAccessHistoryUseCase: GetAdminAccessHistoryUseCase
) : BaseViewModel<AdminAccessHistoryUiState, AdminAccessHistoryUiEvent, AdminAccessHistoryUiEffect>(
    AdminAccessHistoryUiState()
) {

    init {
        onEvent(AdminAccessHistoryUiEvent.LoadHistory)
    }

    override fun onEvent(event: AdminAccessHistoryUiEvent) {
        when (event) {
            is AdminAccessHistoryUiEvent.LoadHistory -> loadHistory()
            is AdminAccessHistoryUiEvent.Refresh -> loadHistory()
        }
    }

    private fun loadHistory() {
        val flow = getAdminAccessHistoryUseCase()
            .cachedIn(viewModelScope)
        
        updateState { it.copy(pagingFlow = flow) }
    }
}
