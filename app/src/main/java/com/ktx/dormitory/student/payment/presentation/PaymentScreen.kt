package com.ktx.dormitory.student.payment.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.util.Locale
import com.ktx.dormitory.ui.components.EmptyView
import com.ktx.dormitory.ui.components.ErrorView
import com.ktx.dormitory.ui.components.LoadingView
import com.ktx.dormitory.student.payment.domain.model.Invoice
import com.ktx.dormitory.student.payment.domain.model.InvoiceType
import com.ktx.dormitory.student.payment.domain.model.PaymentStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // billId là UUID String (không còn là Long)
    var selectedInvoice by remember { mutableStateOf<Invoice?>(null) }

    selectedInvoice?.let { invoice ->
        AlertDialog(
            onDismissRequest = { selectedInvoice = null },
            title = { Text("Xác nhận thanh toán") },
            text = { Text("Bạn có chắc chắn đã chuyển khoản ${formatCurrency(invoice.remainingAmount ?: invoice.amount ?: 0.0)} cho hóa đơn \"${invoice.description ?: "Không có mô tả"}\"? Hành động này sẽ gửi yêu cầu đối soát tới ban quản lý.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyPayment(invoice.id, invoice.remainingAmount ?: invoice.amount ?: 0.0)
                        selectedInvoice = null
                    },
                    modifier = Modifier.testTag("payment_confirm_dialog_confirm")
                ) { Text("Xác nhận") }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedInvoice = null },
                    modifier = Modifier.testTag("payment_confirm_dialog_cancel")
                ) { Text("Hủy") }
            }
        )
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
                is PaymentUiState.Loading -> Box(Modifier.testTag("payment_loading_view")) { LoadingView() }
                is PaymentUiState.Error -> Box(Modifier.testTag("payment_error_view")) { ErrorView(message = state.message, onRetry = { viewModel.loadInvoices() }) }
                is PaymentUiState.Success -> {
                    if (state.invoices.isEmpty()) {
                        Box(Modifier.testTag("payment_empty_view")) { EmptyView(message = "Không có hóa đơn nào cần thanh toán") }
                    } else {
                        PaymentContent(state) { invoice -> selectedInvoice = invoice }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentContent(state: PaymentUiState.Success, onInvoiceClick: (Invoice) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("payment_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TotalAmountCard(state.totalUnpaid)
        }

        item {
            Text(
                text = "Chi tiết hóa đơn",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(state.invoices) { invoice ->
            InvoiceCard(
                invoice = invoice,
                isVerifying = state.isVerifying,
                onVerify = { onInvoiceClick(invoice) }
            )
        }

        if (state.totalUnpaid > 0) {
            item {
                DynamicQrPaymentCard(state.totalUnpaid)
            }
        }
    }
}

@Composable
fun InvoiceCard(invoice: Invoice, isVerifying: Boolean, onVerify: () -> Unit) {
    val statusColor = if (invoice.status == PaymentStatus.PAID) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        modifier = Modifier.fillMaxWidth().testTag("invoice_card_${invoice.id}"),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icon = when(invoice.type) {
                    InvoiceType.ROOM -> Icons.Default.Home
                    InvoiceType.ELECTRICITY -> Icons.Default.FlashOn
                    InvoiceType.WATER -> Icons.Default.WaterDrop
                    InvoiceType.SERVICE -> Icons.Default.MiscellaneousServices
                    InvoiceType.APPLICATION -> Icons.AutoMirrored.Filled.Assignment
                    InvoiceType.PENALTY -> Icons.Default.ReportProblem
                    InvoiceType.DEPOSIT -> Icons.Default.AccountBalanceWallet
                    null -> Icons.Default.Description
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(Modifier.width(16.dp))

                Column(Modifier.weight(1f)) {
                    Text(invoice.description ?: "Hóa đơn", fontWeight = FontWeight.Bold)
                    Text("Hạn: ${invoice.dueDate ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(invoice.amount ?: 0.0),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if ((invoice.remainingAmount ?: 0.0) > 0) {
                        Text(
                            text = "Còn lại: ${formatCurrency(invoice.remainingAmount ?: 0.0)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFF44336)
                        )
                    }
                    Text(
                        text = when (invoice.status) {
                            PaymentStatus.PAID -> "Đã thanh toán"
                            PaymentStatus.PARTIALLY_PAID -> "Thanh toán một phần"
                            PaymentStatus.OVERDUE -> "Quá hạn"
                            else -> "Chưa thanh toán"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
            }

            if (invoice.status == PaymentStatus.UNPAID || invoice.status == PaymentStatus.PARTIALLY_PAID) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onVerify,
                    modifier = Modifier.fillMaxWidth().testTag("invoice_verify_button_${invoice.id}"),
                    contentPadding = PaddingValues(0.dp),
                    enabled = !isVerifying
                ) {
                    if (isVerifying) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Xác nhận đã chuyển khoản", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DynamicQrPaymentCard(amount: Double) {
    val bankId = "MB"
    val accountNo = "123456789"
    val accountName = "KTX SMART DORMITORY"
    val description = "THANH TOAN HOA DON KTX"

    val qrUrl = "https://img.vietqr.io/image/$bankId-$accountNo-compact2.png" +
            "?amount=${amount.toInt()}" +
            "&addInfo=${description.replace(" ", "%20")}" +
            "&accountName=${accountName.replace(" ", "%20")}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .testTag("dynamic_qr_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Quét mã QR để thanh toán nhanh", fontWeight = FontWeight.Bold)
            Text("Tự động điền số tiền & nội dung", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)

            Spacer(Modifier.height(16.dp))

            AsyncImage(
                model = qrUrl,
                contentDescription = "VietQR Payment",
                modifier = Modifier.size(220.dp),
                placeholder = null
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Ngân hàng: $bankId\nSTK: $accountNo\nChủ TK: $accountName",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun TotalAmountCard(total: Double) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().testTag("total_amount_card"),
        shadowElevation = 4.dp
    ) {
        Column(Modifier.padding(24.dp)) {
            Text("Tổng dư nợ hiện tại", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            Text(
                text = formatCurrency(total),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

fun formatCurrency(amount: Double): String = String.format(Locale.getDefault(), "%,.0f VNĐ", amount)
