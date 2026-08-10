package com.ktx.dormitory.presentation.features.auth

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import com.google.common.truth.Truth.assertThat
import com.ktx.dormitory.navigation.Screen
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var viewModel: LoginViewModel
    private val uiState = MutableStateFlow(LoginUiState())

    @Before
    fun setup() {
        viewModel = mockk<LoginViewModel>(relaxed = true)
        every { viewModel.uiState } returns uiState
    }

    @Test
    fun login_with_valid_credentials_triggers_process() {
        startLoginScreen()

        composeTestRule.onNodeWithTag("login_mssv_field").performTextInput("SV001")
        composeTestRule.onNodeWithTag("login_password_field").performTextInput("123456")
        composeTestRule.onNodeWithTag("login_button").performClick()

        verify { viewModel.performLogin("SV001", "123456", any(), any()) }
    }

    @Test
    fun shows_loading_indicator_during_login() {
        uiState.value = LoginUiState(isLoading = true)
        startLoginScreen()

        composeTestRule.onNodeWithTag("login_loading", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("login_button").assertIsNotEnabled()
    }

    @Test
    fun shows_field_errors_when_validation_fails() {
        uiState.value = LoginUiState(
            mssvError = "MSSV không hợp lệ",
            passwordError = "Mật khẩu quá ngắn"
        )
        startLoginScreen()

        composeTestRule.onNodeWithText("MSSV không hợp lệ").assertExists()
        composeTestRule.onNodeWithText("Mật khẩu quá ngắn").assertExists()
    }

    @Test
    fun navigates_to_home_on_successful_login() {
        // GIVEN: Giả lập login thành công
        every { 
            viewModel.performLogin(any(), any(), any(), any()) 
        } answers {
            val onSuccess = it.invocation.args[2] as (String) -> Unit
            onSuccess("USER")
        }
        
        startLoginScreen()

        // WHEN: Thực hiện đăng nhập
        composeTestRule.onNodeWithTag("login_mssv_field").performTextInput("SV001")
        composeTestRule.onNodeWithTag("login_password_field").performTextInput("123456")
        composeTestRule.onNodeWithTag("login_button").performClick()

        // THEN: Kiểm tra xem route hiện tại của NavController có phải là StudentHome không
        assertThat(navController.currentDestination?.route).isEqualTo(Screen.StudentHome.route)
    }

    private fun startLoginScreen() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            // Cần set một Graph tối giản để NavController hoạt động
            // Sử dụng cú pháp không gây hiểu lầm cho trình biên dịch
            val navGraph = navController.createGraph(startDestination = Screen.Login.route) {
                composable(Screen.Login.route) {}
                composable(Screen.StudentHome.route) {}
                composable(Screen.AdminDashboard.route) {}
            }
            navController.graph = navGraph

            LoginScreen(
                navController = navController,
                loginViewModel = viewModel
            )
        }
    }
}
