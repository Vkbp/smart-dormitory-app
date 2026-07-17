package com.ktx.dormitory

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.ktx.dormitory.core.network.NetworkMonitor
import com.ktx.dormitory.navigation.AppNavigation
import com.ktx.dormitory.core.util.AuthEvent
import com.ktx.dormitory.core.util.AuthEventBus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val isOnline by networkMonitor.isOnline.collectAsStateWithLifecycle(initialValue = true)

            // Lắng nghe sự kiện Logout toàn cục
            LaunchedEffect(Unit) {
                AuthEventBus.events.collectLatest { event ->
                    if (event == AuthEvent.LOGOUT) {
                        // Toast.makeText(this@MainActivity, "Đã đăng xuất.", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }

            Column {
                if (!isOnline) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Red)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Đang ngoại tuyến",
                            color = Color.White,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}

