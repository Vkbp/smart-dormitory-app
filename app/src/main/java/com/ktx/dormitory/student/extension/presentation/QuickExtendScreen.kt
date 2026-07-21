package com.ktx.dormitory.student.extension.presentation

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ktx.dormitory.core.util.FileDownloadUtil
import com.ktx.dormitory.student.extension.domain.model.ExtensionReason

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickExtendScreen(
    navController: NavController,
    viewModel: ExtensionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var reason by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gia hạn lưu trú", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isCheckingStatus -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.isLocked -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.LockClock,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Đã khóa đăng ký",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.lockMessage ?: "Hiện tại không trong đợt gia hạn.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Quay lại")
                        }
                    }
                }
                uiState.extensionResponse != null -> {
                    // Success UI
                    val response = uiState.extensionResponse!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Nộp đơn thành công!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Hiển thị trạng thái theo đặc tả .md
                        val isAutoApproved = response.status.uppercase() == "APPROVED" && response.reason == "ROOM_LEADER"
                        
                        val statusText = when {
                            isAutoApproved -> "Đã duyệt (Tự động)"
                            response.status.uppercase() == "PENDING" -> "Đang chờ Ban Quản lý KTX xét duyệt"
                            response.status.uppercase() == "APPROVED" -> "Đơn của bạn đã được duyệt"
                            response.status.uppercase() == "REJECTED" -> "Đơn của bạn đã bị từ chối"
                            else -> "Trạng thái: ${response.status}"
                        }
                        
                        val statusColor = when(response.status.uppercase()) {
                            "PENDING" -> MaterialTheme.colorScheme.primary
                            "APPROVED" -> Color(0xFF4CAF50)
                            "REJECTED" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        // Xử lý logic Đợt Hè vs Đợt Chính Khóa: Đặc tả AGENT.md
                        val isApproved = response.status.uppercase() == "APPROVED"
                        val hasDocs = !response.contractPdfUrl.isNullOrEmpty() || !response.commitmentPdfUrl.isNullOrEmpty()
                        
                        // 1. Tag trạng thái hiện đại
                        Surface(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isApproved) Icons.Default.Verified else Icons.Default.PendingActions,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = statusColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        
                        if (response.status.uppercase() == "REJECTED" && !response.rejectReason.isNullOrEmpty()) {
                            Text(
                                text = "Lý do: ${response.rejectReason}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    "CHI TIẾT ĐƠN HÀNG",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                DetailRow("Mã đơn", response.extensionId.take(8).uppercase())
                                DetailRow("Lý do nộp", response.reason)
                                DetailRow("Phòng hiện tại", response.currentRoomCode ?: "N/A")
                                DetailRow("Giường số", response.currentBedCode ?: "N/A")
                                DetailRow("Ngày nộp", "Vừa xong")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Xử lý logic Đợt Hè vs Chính Khóa theo AGENT.md
                        if (isApproved) {
                            if (hasDocs) {
                                // ĐỢT CHÍNH KHÓA (Dài hạn > 3 tháng) - Hiển thị nút TẢI NỔI BẬT
                                Text(
                                    "TÀI LIỆU CẦN IN VÀ KÝ",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                                )
                                
                                if (!response.contractPdfUrl.isNullOrEmpty()) {
                                    DownloadButton("Tải Hợp Đồng (PDF)", response.contractPdfUrl)
                                }
                                
                                if (!response.commitmentPdfUrl.isNullOrEmpty()) {
                                    DownloadButton("Tải Bản Cam Kết (PDF)", response.commitmentPdfUrl)
                                }
                                
                                Text(
                                    text = "* Vui lòng tải xuống, in ra và ký tên trước khi nộp cho Ban Quản lý.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            } else {
                                // ĐỢT HÈ (Ngắn hạn <= 3 tháng) - Thông báo không cần hợp đồng
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            "Đợt Hè không yêu cầu Hợp đồng cứng. Bạn có thể tiếp tục lưu trú theo lịch.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }

                            if (!response.pdfUrl.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                PdfButton("Xem Quyết Định Gia Hạn", response.pdfUrl)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Quay lại màn hình chính", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> {
                    // Form UI (uiState.extensionResponse == null)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Đơn xin gia hạn",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Vui lòng nhập lý do và mô tả chi tiết mong muốn gia hạn của bạn.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = reason,
                            onValueChange = { reason = it },
                            label = { Text("Lý do gia hạn") },
                            placeholder = { Text("VD: Ở lại học kỳ phụ") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Mô tả chi tiết") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            placeholder = { Text("Nhập lý do cụ thể của bạn...") }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.onEvent(ExtensionUiEvent.SubmitExtension(reason, description)) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading && description.isNotBlank() && reason.isNotBlank(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Nộp Đơn Gia Hạn")
                            }
                        }
                        
                        AnimatedVisibility(visible = uiState.error != null) {
                            Text(
                                text = uiState.error ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DownloadButton(text: String, url: String) {
    val context = LocalContext.current
    
    // Đăng ký Launcher để xin quyền truy cập bộ nhớ (Dành cho các máy cũ Android < 10)
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Bất kể được cấp quyền hay không, ta đều gọi hàm downloadPdf.
        // Bên trong FileDownloadUtil sẽ tự động quyết định:
        // - Nếu có quyền: Tải vào thư mục Downloads chung.
        // - Nếu ko có quyền hoặc lỗi: Tải vào thư mục Private của App.
        FileDownloadUtil.downloadPdf(context, url, text)
    }

    Button(
        onClick = {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // Với Android 9 trở xuống: Cần hiện Popup xin quyền ghi
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                // Với Android 10 trở lên: Không cần xin quyền cho thư mục Downloads, chạy thẳng
                FileDownloadUtil.downloadPdf(context, url, text)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Icon(Icons.Default.Download, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PdfButton(text: String, url: String) {
    val context = LocalContext.current
    Button(
        onClick = {
            val normalizedUrl = FileDownloadUtil.getNormalizedPdfUrl(url)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalizedUrl))
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}
