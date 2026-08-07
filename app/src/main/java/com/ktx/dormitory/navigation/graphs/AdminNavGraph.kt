package com.ktx.dormitory.navigation.graphs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.admin.dashboard.presentation.AdminDashboardScreen
import com.ktx.dormitory.admin.smartaccess.presentation.SmartAccessScreen
import com.ktx.dormitory.admin.face.presentation.FaceApprovalScreen
import com.ktx.dormitory.admin.checkout.presentation.CheckoutApprovalScreen
import com.ktx.dormitory.admin.extension.presentation.StayExtensionScreen
import com.ktx.dormitory.admin.checkin.presentation.CheckInScreen
import com.ktx.dormitory.admin.notification.presentation.NotificationBroadcastScreen
import com.ktx.dormitory.admin.smartaccess.presentation.history.AdminAccessHistoryScreen
import com.ktx.dormitory.admin.smartaccess.presentation.history.AdminAccessDetailScreen
import com.ktx.dormitory.student.access.domain.model.AccessLog
import com.ktx.dormitory.shared.auth.presentation.LoginViewModel

fun NavGraphBuilder.adminNavGraph(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    navigation(
        startDestination = Screen.AdminDashboard.route,
        route = "admin_graph"
    ) {
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController, loginViewModel)
        }
        composable(Screen.AdminAccounts.route) {
            AdminPlaceholderScreen("Account Management")
        }
        composable(Screen.AdminReports.route) {
            AdminPlaceholderScreen("Reports & Statistics")
        }
        composable(Screen.AdminSettings.route) {
            AdminPlaceholderScreen("System Settings")
        }

        // --- Admin Utilities ---
        composable(Screen.AdminSmartAccess.route) {
            SmartAccessScreen(navController)
        }
        composable(Screen.AdminAccessHistory.route) {
            AdminAccessHistoryScreen(navController)
        }
        composable(
            route = Screen.AdminAccessDetail.route,
            arguments = listOf(navArgument("id") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val log = backStackEntry.savedStateHandle.get<AccessLog>("log")
            AdminAccessDetailScreen(navController, log)
        }
        composable(Screen.AdminFaceApproval.route) {
            FaceApprovalScreen(navController)
        }
        composable(Screen.AdminCheckoutApproval.route) {
            CheckoutApprovalScreen(navController)
        }
        composable(Screen.AdminExtensionApproval.route) {
            StayExtensionScreen(navController)
        }
        composable(Screen.AdminCheckIn.route) {
            CheckInScreen(navController)
        }
        composable(Screen.AdminNotificationBroadcast.route) {
            NotificationBroadcastScreen(navController)
        }
    }
}

@Composable
fun AdminPlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.headlineMedium)
            Text(text = "Coming Soon (Admin Role)", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
