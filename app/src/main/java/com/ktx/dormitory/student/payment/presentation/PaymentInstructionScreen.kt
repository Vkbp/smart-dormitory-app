package com.ktx.dormitory.student.payment.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import javax.inject.Inject

data class PaymentInstructionUiState(
    val isLoading: Boolean = false,
    val instruction: PaymentInstruction? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class PaymentInstructionViewModel @Inject constructor(
    private val getPaymentInstructionUseCase: GetPaymentInstructionUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentInstructionUiState())
    val uiState: StateFlow<PaymentInstructionUiState> = _uiState.asStateFlow()

    init {
        loadInstruction()
    }

    fun loadInstruction() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getPaymentInstructionUseCase().onSuccess { result ->
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
                title = { Text("Hướng dẫn chuyển khoản") },
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
                        
                        InstructionRow("Ngân hàng", instr.bankName, context)
                        InstructionRow("Số tài khoản", instr.accountNumber, context, canCopy = true)
                        InstructionRow("Chủ tài khoản", instr.accountHolder, context)
                        InstructionRow("Nội dung", "${instr.contentPrefix ?: "SDMS"}-[MSSV]", context, canCopy = true)
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (instr.qrCodeUrl != null) {
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
                        "Lưu ý: Vui lòng ghi đúng nội dung chuyển khoản để hệ thống tự động xác nhận hóa đơn nhanh nhất.",
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
fun InstructionRow(label: String, value: String, context: Context, canCopy: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        if (canCopy) {
            IconButton(onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label, value)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Đã sao chép $label", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(20.dp))
            }
        }
    }
}
