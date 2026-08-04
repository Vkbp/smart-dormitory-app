package com.ktx.dormitory.student.payment.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ktx.dormitory.student.payment.domain.model.PaymentInstruction
import com.ktx.dormitory.student.payment.domain.usecase.GetPaymentInstructionUseCase
import com.ktx.dormitory.ui.components.LoadingView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Locale
import javax.inject.Inject

data class PaymentInstructionUiState(
    val isLoading: Boolean = false,
    val instruction: PaymentInstruction? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class PaymentInstructionViewModel @Inject constructor(
    private val getPaymentInstructionUseCase: GetPaymentInstructionUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentInstructionUiState())
    val uiState: StateFlow<PaymentInstructionUiState> = _uiState.asStateFlow()

    val billId: String? = savedStateHandle.get<String>("billId")
    val isSpecificBill: Boolean = !billId.isNullOrBlank()

    init {
        loadInstruction()
    }

    fun loadInstruction() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getPaymentInstructionUseCase(billId).onSuccess { result ->
                _uiState.update { it.copy(instruction = result, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInstructionScreen(
    navController: NavController,
    viewModel: PaymentInstructionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.instruction?.amount != null) "Thông tin chuyển khoản" else "Hướng dẫn thanh toán") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingView()
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Lỗi: ${uiState.errorMessage}")
                    Button(onClick = { viewModel.loadInstruction() }) {
                        Text("Thử lại")
                    }
                }
            }
        } else if (uiState.instruction != null) {
            val instr = uiState.instruction!!
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Thêm phần Các bước hướng dẫn nếu là Guide chung
                if (!viewModel.isSpecificBill) {
                    GuideStepsSection()
                    Spacer(Modifier.height(24.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Thông tin tài khoản KTX",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        
                        InstructionRow("Ngân hàng", instr.bankName ?: "N/A", context)
                        InstructionRow("Số tài khoản", instr.accountNumber ?: "N/A", context, canCopy = true)
                        InstructionRow("Chủ tài khoản", instr.accountHolder ?: "N/A", context)
                        
                        // Chỉ hiện số tiền nếu được chỉ định billId cụ thể
                        if (viewModel.isSpecificBill) {
                            instr.amount?.let { amt ->
                                InstructionRow(
                                    label = "Số tiền cần thanh toán",
                                    value = formatCurrency(amt),
                                    context = context,
                                    canCopy = true,
                                    copyValue = amt.toPlainString()
                                )
                            }
                        }

                        InstructionRow(
                            label = "Nội dung chuyển khoản",
                            value = if (viewModel.isSpecificBill) (instr.contentPrefix ?: "SDMS [Mã-Hóa-Đơn]") else "SDMS [Mã-Hóa-Đơn]",
                            context = context,
                            canCopy = viewModel.isSpecificBill && instr.contentPrefix != null && !instr.contentPrefix.contains("["),
                            isHighlighted = viewModel.isSpecificBill
                        )
                    }
                }

                if (instr.qrCodeUrl != null) {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Quét mã QR để thanh toán nhanh",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(16.dp))
                    AsyncImage(
                        model = instr.qrCodeUrl,
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                ) {
                    Text(
                        "Lưu ý: Hệ thống tự động gạch nợ dựa trên Nội dung chuyển khoản. Vui lòng không tự ý thay đổi mã nội dung.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun GuideStepsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "QUY TRÌNH LẤY MÃ TỰ ĐỘNG", 
                style = MaterialTheme.typography.labelLarge, 
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
            StepItem(1, "Tại danh sách Hóa đơn, nhấn vào hóa đơn bạn cần đóng.")
            StepItem(2, "Màn hình chi tiết hiện ra -> Nhấn nút 'Lấy mã QR chuyển khoản ngân hàng'.")
            StepItem(3, "Hệ thống sẽ hiển thị Số tiền và Nội dung chuyển khoản chính xác của riêng hóa đơn đó.")
            StepItem(4, "Sử dụng các nút 'Sao chép' để dán thông tin vào App ngân hàng và thực hiện chuyển khoản.")
        }
    }
}

@Composable
fun StepItem(number: Int, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number.toString(), 
                    color = Color.White, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text = text, 
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun InstructionRow(
    label: String,
    value: String,
    context: Context,
    canCopy: Boolean = false,
    copyValue: String? = null,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = if (isHighlighted) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
                fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.Medium,
                color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        if (canCopy) {
            IconButton(onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label, copyValue ?: value)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Đã sao chép $label", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(20.dp))
            }
        }
    }
}

private fun formatCurrency(amount: BigDecimal): String = String.format(Locale.getDefault(), "%,.0f VNĐ", amount)
