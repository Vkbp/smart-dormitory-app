package com.ktx.dormitory.navigation.graphs

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.student.access.presentation.AccessHistoryScreen
import com.ktx.dormitory.student.access.presentation.AccessViewModel
import com.ktx.dormitory.student.access.presentation.CurfewRequestScreen
import com.ktx.dormitory.student.access.presentation.CreateCurfewRequestScreen
import com.ktx.dormitory.student.face.presentation.FaceRegistrationScreen
import com.ktx.dormitory.student.face.presentation.FaceStatusScreen
import com.ktx.dormitory.student.face.presentation.FaceVerificationHistoryScreen
import com.ktx.dormitory.student.home.presentation.HomeScreen
import com.ktx.dormitory.shared.profile.presentation.ProfileScreen
import com.ktx.dormitory.student.room.presentation.RoomScreen
import com.ktx.dormitory.student.room.presentation.RoomViewModel
import com.ktx.dormitory.student.room.presentation.RoomUtilitiesScreen
import com.ktx.dormitory.student.room.presentation.RoomUtilitiesViewModel
import com.ktx.dormitory.student.room.presentation.RoomTransferScreen
import com.ktx.dormitory.student.room.presentation.RoomTransferViewModel
import com.ktx.dormitory.student.maintenance.presentation.MaintenanceScreen
import com.ktx.dormitory.student.maintenance.presentation.MaintenanceViewModel
import com.ktx.dormitory.student.maintenance.presentation.CreateMaintenanceScreen
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
            val activity = LocalContext.current as FragmentActivity
            val notificationViewModel: NotificationViewModel = hiltViewModel(activity)
            HomeScreen(navController, notificationViewModel = notificationViewModel)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Screen.RoomInfo.route) {
            val roomViewModel: RoomViewModel = hiltViewModel()
            RoomScreen(navController, roomViewModel)
        }

        composable(Screen.RoomUtilities.route) {
            RoomUtilitiesScreen(navController)
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

        composable(Screen.CreateCurfewRequest.route) {
            val accessViewModel: AccessViewModel = hiltViewModel()
            CreateCurfewRequestScreen(navController, accessViewModel)
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
            val activity = LocalContext.current as FragmentActivity
            val notificationViewModel: NotificationViewModel = hiltViewModel(activity)
            NotificationScreen(navController, notificationViewModel)
        }

        composable(Screen.FaceStatus.route) {
            FaceStatusScreen(navController)
        }

        composable(Screen.FaceVerificationHistory.route) {
            FaceVerificationHistoryScreen(navController)
        }

        composable(
            route = Screen.PaymentInstruction.route + "?billId={billId}",
            arguments = listOf(
                navArgument("billId") {
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            PaymentInstructionScreen(navController)
        }

        composable(Screen.RoomTransfer.route) {
            val roomTransferViewModel: RoomTransferViewModel = hiltViewModel()
            RoomTransferScreen(navController, roomTransferViewModel)
        }

        composable(Screen.Maintenance.route) {
            val maintenanceViewModel: MaintenanceViewModel = hiltViewModel()
            MaintenanceScreen(navController, maintenanceViewModel)
        }

        composable(Screen.CreateMaintenance.route) {
            val maintenanceViewModel: MaintenanceViewModel = hiltViewModel()
            CreateMaintenanceScreen(navController, maintenanceViewModel)
        }
    }
}
