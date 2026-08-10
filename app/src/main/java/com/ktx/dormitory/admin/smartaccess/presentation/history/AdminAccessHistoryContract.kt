package com.ktx.dormitory.admin.smartaccess.presentation.history

import androidx.paging.PagingData
import com.ktx.dormitory.core.base.BaseContract
import com.ktx.dormitory.student.access.domain.model.AccessLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class AdminAccessHistoryUiState(
    val pagingFlow: Flow<PagingData<AccessLog>> = emptyFlow(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : BaseContract.State

sealed class AdminAccessHistoryUiEvent : BaseContract.Event {
    data object LoadHistory : AdminAccessHistoryUiEvent()
    data object Refresh : AdminAccessHistoryUiEvent()
}

sealed class AdminAccessHistoryUiEffect : BaseContract.Effect {
    data class ShowToast(val message: String) : AdminAccessHistoryUiEffect()
}
