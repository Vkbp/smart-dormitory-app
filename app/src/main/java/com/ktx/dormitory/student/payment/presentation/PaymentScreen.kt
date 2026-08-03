package com.ktx.dormitory.student.payment.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ktx.dormitory.navigation.Screen
import com.ktx.dormitory.student.payment.domain.model.Bill
import com.ktx.dormitory.student.payment.domain.model.BillStatus
import com.ktx.dormitory.student.payment.domain.model.BillType
import com.ktx.dormitory.student.payment.presentation.components.SmartQRBottomSheet
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.LoadingView
import java.math.BigDecimal
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is PaymentUiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is PaymentUiEffect.NavigateToSuccess -> {
                    // Chuyển đến màn hình thành công hoặc thông báo
                    Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thanh toán hóa đơn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadInvoices() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                    IconButton(onClick = { navController.navigate(Screen.PaymentHistory.route) }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .testTag("payment_screen_container")) {
            
            when (val state = uiState) {
                is PaymentUiState.Loading -> LoadingView()
                is PaymentUiState.Error -> ErrorView(message = state.message, onRetry = { viewModel.loadInvoices() })
                is PaymentUiState.Success -> {
                    if (state.bills.isEmpty()) {
                        EmptyView(message = "Không có hóa đơn nào cần thanh toán")
                    } else {
                        PaymentContent(
                            state = state,
                            navController = navController,
                            onPayClick = { bill -> 
                                viewModel.createSmartQR(bill.id, bill.billCode ?: "", bill.remainingAmount ?: bill.amount ?: BigDecimal.ZERO)
                            }
                        )

                        // Hiển thị BottomSheet QR nếu có kết quả
                        state.smartQR?.let { qrResult ->
                            SmartQRBottomSheet(
                                paymentResult = qrResult,
                                onDismiss = { viewModel.stopPolling() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentContent(
    state: PaymentUiState.Success,
    navController: NavController,
    onPayClick: (Bill) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TotalAmountCard(state.totalUnpaid)
        }

        item {
            Text(
                text = "Hóa đơn cần thanh toán",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
        }

        items(state.bills) { bill ->
            BillCard(
                bill = bill,
                isProcessing = state.isProcessing,
                onPay = { onPayClick(bill) }
            )
        }
        
        item {
            Spacer(Modifier.height(16.dp))
            OutlinedCard(
                onClick = { navController.navigate(Screen.PaymentInstruction.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(16.dp))
                    Text("Hướng dẫn chuyển khoản thủ công", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun BillCard(bill: Bill, isProcessing: Boolean, onPay: () -> Unit) {
    val statusColor = when (bill.status) {
        BillStatus.PAID -> Color(0xFF4CAF50)
        BillStatus.PARTIALLY_PAID -> Color(0xFFFFC107)
        BillStatus.OVERDUE -> Color(0xFFF44336)
        BillStatus.CANCELLED -> Color.Gray
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (bill.type) {
                    BillType.ACCOMMODATION_FEE -> Icons.Default.Home
                    BillType.ELECTRIC_FEE -> Icons.Default.FlashOn
                    BillType.WATER_FEE -> Icons.Default.WaterDrop
                    BillType.APPLICATION_FEE -> Icons.AutoMirrored.Filled.Assignment
                    BillType.PENALTY_FEE -> Icons.Default.ReportProblem
                    BillType.DEPOSIT_FEE -> Icons.Default.AccountBalanceWallet
                    else -> Icons.Default.Description
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(Modifier.width(16.dp))

                Column(Modifier.weight(1f)) {
                    Text(bill.description ?: "Hóa đơn KTX", fontWeight = FontWeight.Bold)
                    if (!bill.billCode.isNullOrBlank()) {
                        Text("Mã: ${bill.billCode}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Text("Hạn: ${bill.dueDate ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                    if ((bill.paidAmount ?: BigDecimal.ZERO) > BigDecimal.ZERO) {
                        Text(
                            text = "Đã đóng: ${formatCurrency(bill.paidAmount!!)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(bill.remainingAmount ?: bill.amount ?: BigDecimal.ZERO),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when (bill.status) {
                            BillStatus.UNPAID -> "Chưa thanh toán"
                            BillStatus.PARTIALLY_PAID -> "Thanh toán một phần"
                            BillStatus.OVERDUE -> "Quá hạn"
                            else -> bill.status.toString()
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
            }

            if (bill.status == BillStatus.UNPAID || bill.status == BillStatus.PARTIALLY_PAID) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onPay,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("TẠO MÃ QR THÔNG MINH", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TotalAmountCard(total: BigDecimal) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Column(Modifier.padding(24.dp)) {
            Text("Tổng dư nợ cần thanh toán", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            Text(
                text = formatCurrency(total),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

private fun formatCurrency(amount: BigDecimal): String = String.format(Locale.getDefault(), "%,.0f VNĐ", amount)
