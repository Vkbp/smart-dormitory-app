package com.ktx.dormitory.presentation.features.admin.smartaccess

import com.ktx.dormitory.domain.admin.usecase.EmergencyOverrideUseCase
import com.ktx.dormitory.domain.admin.usecase.RemoteUnlockUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class SmartAccessViewModelTest {

    private val remoteUnlockUseCase: RemoteUnlockUseCase = mockk()
    private val emergencyOverrideUseCase: EmergencyOverrideUseCase = mockk()
    private lateinit var viewModel: SmartAccessViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SmartAccessViewModel(remoteUnlockUseCase, emergencyOverrideUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `remote unlock success updates state`() = runTest {
        val gateId = UUID.randomUUID()
        val buildingId = UUID.randomUUID()
        coEvery { remoteUnlockUseCase(gateId, buildingId) } returns Result.success(Unit)

        viewModel.onEvent(SmartAccessUiEvent.RemoteUnlock(gateId, buildingId))
        
        // Initial state is loading
        assertEquals(true, viewModel.uiState.value.isLoading)
        
        advanceUntilIdle()
        
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Cửa đã được mở thành công", viewModel.uiState.value.successMessage)
    }

    @Test
    fun `remote unlock failure updates state`() = runTest {
        val gateId = UUID.randomUUID()
        val buildingId = UUID.randomUUID()
        coEvery { remoteUnlockUseCase(gateId, buildingId) } returns Result.failure(Exception("Error"))

        viewModel.onEvent(SmartAccessUiEvent.RemoteUnlock(gateId, buildingId))
        
        advanceUntilIdle()
        
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Error", viewModel.uiState.value.errorMessage)
    }
}
