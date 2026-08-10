package com.ktx.dormitory.presentation.features.request

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.ktx.dormitory.domain.model.DormRequest
import com.ktx.dormitory.domain.model.RequestStatus
import com.ktx.dormitory.domain.model.RequestType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RequestScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var viewModel: RequestViewModel
    private val uiState = MutableStateFlow(RequestUiState())
    private val formState = MutableStateFlow(RequestFormState())

    @Before
    fun setup() {
        viewModel = mockk<RequestViewModel>(relaxed = true)
        every { viewModel.uiState } returns uiState
        every { viewModel.formState } returns formState
    }

    @Test
    fun shows_empty_view_when_no_requests() {
        uiState.value = RequestUiState(requests = emptyList())
        startRequestScreen()

        composeTestRule.onNodeWithTag("request_empty_view").assertExists()
    }

    @Test
    fun shows_request_list_when_available() {
        val requests = listOf(
            DormRequest("1", "Nguyen A", "SV01", RequestType.REPAIR, "Hong dien", RequestStatus.PENDING, "2023-10-27T10:00:00Z")
        )
        uiState.value = RequestUiState(requests = requests)
        startRequestScreen()

        composeTestRule.onNodeWithTag("request_card_1").assertExists()
        composeTestRule.onNodeWithText("Hỏng điện").assertExists()
    }

    @Test
    fun typing_content_updates_viewmodel() {
        startRequestScreen()

        composeTestRule.onNodeWithTag("request_content_field").performTextInput("Test request content")
        verify { viewModel.onContentChange("Test request content") }
    }

    @Test
    fun clicking_submit_shows_confirm_dialog() {
        formState.value = RequestFormState(content = "Something")
        startRequestScreen()

        composeTestRule.onNodeWithTag("request_submit_button").performClick()
        composeTestRule.onNodeWithText("Gửi yêu cầu?").assertIsDisplayed()
    }

    @Test
    fun confirming_dialog_triggers_submission() {
        startRequestScreen()

        // Mở dialog
        composeTestRule.onNodeWithTag("request_submit_button").performClick()
        
        // Xác nhận
        composeTestRule.onNodeWithTag("request_confirm_dialog_confirm").performClick()
        
        verify { viewModel.submitRequest() }
    }

    private fun startRequestScreen() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            RequestScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
