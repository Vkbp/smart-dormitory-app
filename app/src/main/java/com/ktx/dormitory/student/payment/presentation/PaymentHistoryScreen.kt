package com.ktx.dormitory.student.payment.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ktx.dormitory.core.util.DataFormatter
import com.ktx.dormitory.core.util.DateTimeUtils
import com.ktx.dormitory.student.payment.domain.model.Bill
import com.ktx.dormitory.student.payment.domain.model.BillStatus
import com.ktx.dormitory.student.payment.domain.model.BillType
import com.ktx.dormitory.navigation.components.EmptyView
import com.ktx.dormitory.navigation.components.ErrorView
import com.ktx.dormitory.navigation.components.LoadingView
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(navController: NavController, viewModel: PaymentHistoryViewModel) {
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử hóa đơn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { pagingItems.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                pagingItems.loadState.refresh is LoadState.Loading -> LoadingView()
                pagingItems.loadState.refresh is LoadState.Error -> ErrorView(
                    message = "Không thể tải lịch sử thanh toán",
                    onRetry = { pagingItems.refresh() }
                )
                pagingItems.itemCount == 0 && pagingItems.loadState.refresh !is LoadState.Loading -> {
                    EmptyView(message = "Chưa có hóa đơn nào", icon = Icons.AutoMirrored.Filled.ReceiptLong)
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                        items(
                            count = pagingItems.itemCount,
                            key = pagingItems.itemKey { it.id }
                        ) { index ->
                            val bill = pagingItems[index]
                            if (bill != null) {
                                BillHistoryItem(bill)
                            }
                        }
                        
                        if (pagingItems.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BillHistoryItem(bill: Bill) {
    val statusColor = when (bill.status) {
        BillStatus.PAID -> Color(0xFF4CAF50)
        BillStatus.PARTIALLY_PAID -> Color(0xFFFFC107)
        BillStatus.OVERDUE -> Color(0xFFF44336)
        BillStatus.CANCELLED -> Color.Gray
        else -> MaterialTheme.colorScheme.error
    }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when(bill.type) {
                        BillType.ACCOMMODATION_FEE -> "Tiền phòng"
                        BillType.ELECTRIC_FEE -> "Tiền điện"
                        BillType.PENALTY_FEE -> "Phí phạt"
                        else -> "Hóa đơn"
                    }, 
                    fontWeight = FontWeight.Bold
                )
                if (!bill.billCode.isNullOrBlank()) {
                    Text("Mã: ${bill.billCode}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                if (!bill.description.isNullOrBlank()) {
                    Text(bill.description, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    text = "Hạn: ${DateTimeUtils.formatIsoDate(bill.dueDate)}", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = Color.Gray
                )
                if ((bill.paidAmount ?: BigDecimal.ZERO) > BigDecimal.ZERO) {
                    Text(
                        text = "Đã đóng: ${DataFormatter.formatCurrency(bill.paidAmount!!)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = DataFormatter.formatCurrency(bill.amount ?: BigDecimal.ZERO),
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when(bill.status) {
                        BillStatus.PAID -> "ĐÃ THANH TOÁN"
                        BillStatus.UNPAID -> "CHƯA THANH TOÁN"
                        BillStatus.PARTIALLY_PAID -> "MỘT PHẦN"
                        BillStatus.OVERDUE -> "QUÁ HẠN"
                        BillStatus.CANCELLED -> "ĐÃ HỦY"
                        else -> "N/A"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
