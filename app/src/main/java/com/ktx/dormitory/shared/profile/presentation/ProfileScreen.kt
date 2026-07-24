package com.ktx.dormitory.shared.profile.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ktx.dormitory.core.util.ImageUtil
import com.ktx.dormitory.ui.components.LoadingView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isEditing by remember { mutableStateOf(false) }

    // Editable states
    var phone by remember { mutableStateOf("") }
    var permanentAddress by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var fatherPhone by remember { mutableStateOf("") }
    var motherName by remember { mutableStateOf("") }
    var motherPhone by remember { mutableStateOf("") }

    LaunchedEffect(uiState.profile) {
        uiState.profile?.let {
            phone = it.phone ?: ""
            permanentAddress = it.permanentAddress ?: ""
            emergencyContact = it.emergencyContact ?: ""
            fatherName = it.fatherName ?: ""
            fatherPhone = it.fatherPhone ?: ""
            motherName = it.motherName ?: ""
            motherPhone = it.motherPhone ?: ""
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProfileUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    if (effect.message == "Cập nhật thành công") {
                        isEditing = false
                    }
                }
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = ImageUtil.uriToFile(context, it)
            file?.let { f ->
                viewModel.onEvent(ProfileUiEvent.UploadAvatar(f.absolutePath))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ sinh viên", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            viewModel.onEvent(ProfileUiEvent.UpdateProfile(
                                phone = phone,
                                permanentAddress = permanentAddress,
                                emergencyContact = emergencyContact,
                                fatherName = fatherName,
                                fatherPhone = fatherPhone,
                                motherName = motherName,
                                motherPhone = motherPhone
                            ))
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Lưu", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { isEditing = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Hủy")
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Sửa hồ sơ")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.profile == null) {
            LoadingView()
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar Section
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = uiState.profile?.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { photoPickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                        modifier = Modifier.size(28.dp).offset(x = (-2).dp, y = (-2).dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    if (uiState.isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(100.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = uiState.profile?.fullName ?: "N/A",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = uiState.profile?.studentCode ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Thông tin cá nhân (Editable)
                ProfileSectionTitle(title = "Thông tin liên lạc")
                EditableInfoField(
                    label = "Số điện thoại",
                    value = phone,
                    onValueChange = { phone = it },
                    isEditing = isEditing,
                    icon = Icons.Default.Phone,
                    error = uiState.phoneError
                )
                EditableInfoField(
                    label = "Địa chỉ thường trú",
                    value = permanentAddress,
                    onValueChange = { permanentAddress = it },
                    isEditing = isEditing,
                    icon = Icons.Default.Home
                )
                EditableInfoField(
                    label = "Địa chỉ liên hệ (Gia đình)",
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    isEditing = isEditing,
                    icon = Icons.Default.LocationOn
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Thông tin phụ huynh (Editable)
                ProfileSectionTitle(title = "Thông tin phụ huynh")
                EditableInfoField(
                    label = "Họ tên Cha",
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    isEditing = isEditing,
                    icon = Icons.Default.Person
                )
                EditableInfoField(
                    label = "SĐT Cha",
                    value = fatherPhone,
                    onValueChange = { fatherPhone = it },
                    isEditing = isEditing,
                    icon = Icons.Default.Phone
                )
                EditableInfoField(
                    label = "Họ tên Mẹ",
                    value = motherName,
                    onValueChange = { motherName = it },
                    isEditing = isEditing,
                    icon = Icons.Default.Person
                )
                EditableInfoField(
                    label = "SĐT Mẹ",
                    value = motherPhone,
                    onValueChange = { motherPhone = it },
                    isEditing = isEditing,
                    icon = Icons.Default.Phone
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Thông tin hành chính (Read-Only)
                ProfileSectionTitle(title = "Thông tin hành chính")
                ReadOnlyInfoField(label = "Họ và tên", value = uiState.profile?.fullName ?: "N/A", icon = Icons.Default.Badge)
                ReadOnlyInfoField(label = "Mã số sinh viên", value = uiState.profile?.studentCode ?: "N/A", icon = Icons.Default.Numbers)
                ReadOnlyInfoField(label = "Số CCCD", value = uiState.profile?.citizenId ?: "N/A", icon = Icons.Default.CreditCard)
                ReadOnlyInfoField(label = "Email sinh viên", value = uiState.profile?.email ?: "N/A", icon = Icons.Default.Email)
                ReadOnlyInfoField(label = "Khoa", value = uiState.profile?.faculty ?: "N/A", icon = Icons.Default.School)
                ReadOnlyInfoField(label = "Niên khóa", value = uiState.profile?.academicYear ?: "N/A", icon = Icons.Default.DateRange)
                ReadOnlyInfoField(label = "Mã thẻ từ (RFID)", value = uiState.profile?.rfidCode ?: "Chưa cấp", icon = Icons.Default.VpnKey)
                ReadOnlyInfoField(label = "Trạng thái", value = uiState.profile?.status ?: "N/A", icon = Icons.Default.Info)

                Spacer(modifier = Modifier.height(16.dp))

                // Warning Text
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lưu ý: Các thông tin hành chính (Họ tên, MSSV, CCCD, Khoa, Khóa) đã được khóa để đảm bảo tính pháp lý. Nếu có sai sót hoặc cần thay đổi, sinh viên vui lòng liên hệ trực tiếp Ban Quản Lý KTX để được hỗ trợ cập nhật.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Justify,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.onEvent(ProfileUiEvent.Logout) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun EditableInfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    icon: ImageVector,
    error: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
                isError = error != null,
                supportingText = error?.let { { Text(it) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        } else {
            ReadOnlyInfoField(label = label, value = value.ifBlank { "Chưa cập nhật" }, icon = icon)
        }
    }
}

@Composable
fun ReadOnlyInfoField(
    label: String,
    value: String,
    icon: ImageVector
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
