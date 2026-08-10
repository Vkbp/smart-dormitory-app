package com.ktx.dormitory.presentation.features.notification

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.ktx.dormitory.domain.model.Notification
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var viewModel: NotificationViewModel
    private val uiState = MutableStateFlow(NotificationUiState())

    @Before
    fun setup() {
        viewModel = mockk<NotificationViewModel>(relaxed = true)
        every { viewModel.uiState } returns uiState
    }

    @Test
    fun shows_loading_view_when_loading() {
        uiState.value = NotificationUiState(isLoading = true, notifications = emptyList())
        startNotificationScreen()

        composeTestRule.onNodeWithTag("notification_loading_view").assertExists()
    }

    @Test
    fun shows_notification_list_when_data_is_present() {
        val notifications = listOf(
            Notification("1", "Test Title", "Test Message", "10:00", false)
        )
        uiState.value = NotificationUiState(isLoading = false, notifications = notifications)
        startNotificationScreen()

        composeTestRule.onNodeWithTag("notification_item_1").assertExists()
        composeTestRule.onNodeWithText("Test Title").assertExists()
        composeTestRule.onNodeWithTag("notification_unread_indicator_1").assertExists()
    }

    @Test
    fun shows_empty_view_when_no_notifications() {
        uiState.value = NotificationUiState(isLoading = false, notifications = emptyList())
        startNotificationScreen()

        composeTestRule.onNodeWithTag("notification_empty_view").assertExists()
    }

    private fun startNotificationScreen() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            NotificationScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
