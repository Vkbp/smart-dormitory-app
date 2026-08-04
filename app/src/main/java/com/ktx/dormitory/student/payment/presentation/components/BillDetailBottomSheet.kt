package com.ktx.dormitory.student.payment.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
    onManualPaymentClick: (String) -> Unit,
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
            if (bill.bedCode != null) {
                BillInfoRow("Gán cho giường", bill.bedCode)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (bill.status != BillStatus.PAID) {
                OutlinedButton(
                    onClick = { onDismiss(); onManualPaymentClick(bill.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Lấy mã QR chuyển khoản ngân hàng")
                }
            }

            if (bill.requiresRefund) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Bạn đã thanh toán dư tiền cho hóa đơn này. Bộ phận kế toán đang xử lý hoàn tiền hoặc cấn trừ. Vui lòng liên hệ Văn phòng KTX để biết thêm chi tiết.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (bill.isSplittable) {
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
                        // Tính toán số tiền gợi ý (Tổng / Số thành viên + Trưởng phòng)
                        val totalMembers = roommates.size + 1
                        val suggestedAmount = (bill.amount ?: BigDecimal.ZERO).divide(BigDecimal(totalMembers), 0, java.math.RoundingMode.HALF_UP)
                        
                        // Gợi ý số tiền nếu chưa nhập
                        LaunchedEffect(showReportForm) {
                            if (amountPerStudent.isBlank()) {
                                amountPerStudent = suggestedAmount.toInt().toString()
                            }
                        }

                        LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                            items(roommates) { roommate ->
                                val isReported = bill.reportedStudentIds.contains(roommate.id)
                                val isEligible = (roommate.roomRole == "MEMBER" || roommate.roomRole == "DEPUTY") && !isReported
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Checkbox(
                                        checked = selectedStudentIds.contains(roommate.id) || isReported,
                                        onCheckedChange = { checked ->
                                            selectedStudentIds = if (checked) {
                                                selectedStudentIds + roommate.id
                                            } else {
                                                selectedStudentIds - roommate.id
                                            }
                                        },
                                        enabled = isEligible
                                    )
                                    AsyncImage(
                                        model = roommate.avatarUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(roommate.fullName, fontWeight = FontWeight.SemiBold)
                                            if (isReported) {
                                                Spacer(Modifier.width(8.dp))
                                                Surface(
                                                    color = MaterialTheme.colorScheme.errorContainer,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "Đã báo cáo",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "MSSV: ${roommate.studentCode} - Giường: ${roommate.bedCode ?: "N/A"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                        
                        OutlinedTextField(
                            value = amountPerStudent,
                            onValueChange = { amountPerStudent = it },
                            label = { Text("Số tiền mỗi người phải đóng") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            suffix = { Text("VNĐ") },
                            supportingText = {
                                Text("Gợi ý: ${formatCurrency(suggestedAmount)}")
                            }
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        val currentAmount = amountPerStudent.toBigDecimalOrNull() ?: BigDecimal.ZERO
                        val totalSplit = currentAmount.multiply(BigDecimal(selectedStudentIds.size))
                        val isInvalidAmount = totalSplit >= (bill.amount ?: BigDecimal.ZERO)

                        if (isInvalidAmount && selectedStudentIds.isNotEmpty()) {
                            Text(
                                "Tổng tiền tách nợ phải nhỏ hơn tổng hóa đơn!",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        Button(
                            onClick = { 
                                if (selectedStudentIds.isNotEmpty() && amountPerStudent.isNotBlank() && !isInvalidAmount) {
                                    showConfirmDialog = true 
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSplitLoading && selectedStudentIds.isNotEmpty() && amountPerStudent.isNotBlank() && !isInvalidAmount,
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
                Text("Sau khi tách nợ, số tiền này sẽ bị trừ khỏi hóa đơn gốc của bạn và sinh ra các hóa đơn phạt cho những người bạn đã chọn. Nếu họ không đóng sau 7 ngày quá hạn, họ sẽ bị tự động Check-out khỏi KTX.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onSplitSubmit(selectedStudentIds.toList(), BigDecimal(amountPerStudent))
                }) {
                    Text("ĐỒNG Ý TÁCH NỢ", color = Color.Red)
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
