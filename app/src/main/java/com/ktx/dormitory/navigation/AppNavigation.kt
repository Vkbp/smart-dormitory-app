package com.ktx.dormitory.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ktx.dormitory.shared.auth.presentation.ChangePasswordScreen
import com.ktx.dormitory.shared.auth.presentation.LoginScreen
import com.ktx.dormitory.shared.auth.presentation.LoginViewModel
import com.ktx.dormitory.shared.auth.presentation.SplashScreen
import com.ktx.dormitory.shared.auth.presentation.ForgotPasswordScreen
import com.ktx.dormitory.navigation.graphs.studentNavGraph
import com.ktx.dormitory.navigation.graphs.adminNavGraph
import com.ktx.dormitory.ui.theme.SmartDormTheme

@Composable
fun AppNavigation(navController: NavHostController) {
    val loginViewModel: LoginViewModel = hiltViewModel()

    SmartDormTheme {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController, loginViewModel)
            }

            composable(Screen.Login.route) {
                LoginScreen(navController, loginViewModel)
            }

            composable(Screen.ChangePassword.route) {
                ChangePasswordScreen(navController)
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(navController)
            }

            // --- NESTED GRAPHS BY ROLE ---
            studentNavGraph(navController, loginViewModel)
            adminNavGraph(navController, loginViewModel)
        }
    }
}
