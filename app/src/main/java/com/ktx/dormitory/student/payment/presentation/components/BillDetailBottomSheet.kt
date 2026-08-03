package com.ktx.dormitory.student.payment.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ktx.dormitory.student.payment.domain.model.Bill
import com.ktx.dormitory.student.payment.domain.model.BillStatus
import com.ktx.dormitory.student.payment.domain.model.BillType
import com.ktx.dormitory.student.room.domain.model.Roommate
import java.math.BigDecimal
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailBottomSheet(
    bill: Bill,
    roommates: List<Roommate>?,
    isSplitLoading: Boolean,
    onSplitSubmit: (List<String>, BigDecimal) -> Unit,
    onDismiss: () -> Unit
) {
    var showReportForm by remember { mutableStateOf(false) }
    var selectedStudentIds by remember { mutableStateOf(setOf<String>()) }
    var amountPerStudent by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "Chi tiết hóa đơn",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BillInfoRow("Mã hóa đơn", bill.billCode ?: bill.id.take(8))
            BillInfoRow("Loại", when(bill.type) {
                BillType.ELECTRIC_FEE -> "Tiền điện"
                BillType.ACCOMMODATION_FEE -> "Tiền phòng"
                BillType.PENALTY_FEE -> "Phí phạt"
                else -> "Khác"
            })
            BillInfoRow("Số tiền", formatCurrency(bill.amount ?: BigDecimal.ZERO))
            BillInfoRow("Đã đóng", formatCurrency(bill.paidAmount ?: BigDecimal.ZERO))
            BillInfoRow("Còn lại", formatCurrency(bill.remainingAmount ?: BigDecimal.ZERO))
            BillInfoRow("Hạn đóng", bill.dueDate ?: "N/A")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (bill.type == BillType.ELECTRIC_FEE && bill.isBillOwner && bill.status != BillStatus.PAID) {
                if (!showReportForm) {
                    Button(
                        onClick = { showReportForm = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Báo cáo thành viên không đóng tiền")
                    }
                } else {
                    Text(
                        text = "Chọn thành viên không đóng tiền",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (roommates == null) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                            items(roommates) { roommate ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = selectedStudentIds.contains(roommate.id),
                                        onCheckedChange = { checked ->
                                            selectedStudentIds = if (checked) {
                                                selectedStudentIds + roommate.id
                                            } else {
                                                selectedStudentIds - roommate.id
                                            }
                                        }
                                    )
                                    Text(roommate.fullName)
                                }
                            }
                        }
                        
                        OutlinedTextField(
                            value = amountPerStudent,
                            onValueChange = { amountPerStudent = it },
                            label = { Text("Số tiền mỗi người phải đóng") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            suffix = { Text("VNĐ") }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { 
                                if (selectedStudentIds.isNotEmpty() && amountPerStudent.isNotBlank()) {
                                    showConfirmDialog = true 
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSplitLoading && selectedStudentIds.isNotEmpty() && amountPerStudent.isNotBlank(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isSplitLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            else Text("XÁC NHẬN BÁO CÁO")
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
            title = { Text("Xác nhận báo cáo") },
            text = { 
                Text("Bạn có chắc chắn muốn báo cáo? Hệ thống sẽ tạo hóa đơn phạt cho những người này. Nếu họ không đóng sau 7 ngày quá hạn, họ sẽ bị tự động Check-out khỏi KTX.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onSplitSubmit(selectedStudentIds.toList(), BigDecimal(amountPerStudent))
                }) {
                    Text("ĐỒNG Ý", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("HỦY")
                }
            }
        )
    }
}

@Composable
fun BillInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

private fun formatCurrency(amount: BigDecimal): String = String.format(Locale.getDefault(), "%,.0f VNĐ", amount)
