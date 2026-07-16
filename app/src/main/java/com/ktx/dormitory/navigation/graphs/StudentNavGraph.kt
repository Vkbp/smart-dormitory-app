package com.ktx.dormitory.navigation.graphs

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.student.access.presentation.AccessHistoryScreen
import com.ktx.dormitory.student.access.presentation.AccessViewModel
import com.ktx.dormitory.student.access.presentation.CurfewRequestScreen
import com.ktx.dormitory.student.face.presentation.FaceRegistrationScreen
import com.ktx.dormitory.student.face.presentation.FaceStatusScreen
import com.ktx.dormitory.student.face.presentation.FaceVerificationHistoryScreen
import com.ktx.dormitory.student.home.presentation.HomeScreen
import com.ktx.dormitory.shared.profile.presentation.ProfileScreen
import com.ktx.dormitory.student.room.presentation.RoomScreen
import com.ktx.dormitory.student.room.presentation.RoomViewModel
import com.ktx.dormitory.student.room.presentation.RoomTransferScreen
import com.ktx.dormitory.student.room.presentation.RoomTransferViewModel
import com.ktx.dormitory.student.payment.presentation.PaymentScreen
import com.ktx.dormitory.student.payment.presentation.PaymentHistoryScreen
import com.ktx.dormitory.student.payment.presentation.PaymentHistoryViewModel
import com.ktx.dormitory.student.payment.presentation.PaymentInstructionScreen
import com.ktx.dormitory.student.extension.presentation.QuickExtendScreen
import com.ktx.dormitory.student.extension.presentation.ExtensionViewModel
import com.ktx.dormitory.student.checkout.presentation.CheckoutScreen
import com.ktx.dormitory.student.checkout.presentation.CheckoutViewModel
import com.ktx.dormitory.shared.notification.presentation.NotificationScreen
import com.ktx.dormitory.shared.notification.presentation.NotificationViewModel
import com.ktx.dormitory.shared.notification.presentation.components.IssueHistoryScreen
import com.ktx.dormitory.shared.notification.presentation.components.IssueHistoryViewModel
import com.ktx.dormitory.shared.auth.presentation.LoginViewModel

fun NavGraphBuilder.studentNavGraph(
    navController: NavController,
    loginViewModel: LoginViewModel,
) {
    navigation(
        startDestination = Screen.StudentHome.route,
        route = "student_graph"
    ) {
        composable(Screen.StudentHome.route) {
            HomeScreen(navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.RoomInfo.route) {
            val roomViewModel: RoomViewModel = hiltViewModel()
            RoomScreen(navController, roomViewModel)
        }

        composable(Screen.PaymentHistory.route) {
            val paymentHistoryViewModel: PaymentHistoryViewModel = hiltViewModel()
            PaymentHistoryScreen(navController, paymentHistoryViewModel)
        }

        composable(Screen.Payment.route) { 
            PaymentScreen(navController) 
        }
        
        composable(Screen.AccessHistory.route) {
            AccessHistoryScreen(navController = navController)
        }

        composable(Screen.CurfewRequest.route) {
            val accessViewModel: AccessViewModel = hiltViewModel()
            CurfewRequestScreen(navController, accessViewModel)
        }
        
        composable(Screen.FaceRegistration.route) {
            FaceRegistrationScreen(navController, loginViewModel)
        }

        composable(Screen.QuickExtend.route) {
            val extensionViewModel: ExtensionViewModel = hiltViewModel()
            QuickExtendScreen(navController, extensionViewModel)
        }

        composable(Screen.Checkout.route) {
            val checkoutViewModel: CheckoutViewModel = hiltViewModel()
            CheckoutScreen(navController, checkoutViewModel)
        }

        composable(Screen.Notifications.route) {
            val notificationViewModel: NotificationViewModel = hiltViewModel()
            NotificationScreen(navController, notificationViewModel)
        }

        composable(Screen.FaceStatus.route) {
            FaceStatusScreen(navController)
        }

        composable(Screen.FaceVerificationHistory.route) {
            FaceVerificationHistoryScreen(navController)
        }

        composable(Screen.PaymentInstruction.route) {
            PaymentInstructionScreen(navController)
        }

        composable(Screen.RoomTransfer.route) {
            val roomTransferViewModel: RoomTransferViewModel = hiltViewModel()
            RoomTransferScreen(navController, roomTransferViewModel)
        }

        composable(Screen.IssueHistory.route) {
            val issueHistoryViewModel: IssueHistoryViewModel = hiltViewModel()
            IssueHistoryScreen(navController, issueHistoryViewModel)
        }
    }
}
