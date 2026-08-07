package com.ktx.dormitory.student.face.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ktx.dormitory.ai.core.FaceAnalyzer
import com.ktx.dormitory.ai.processing.liveness.FaceLivenessUiState
import com.ktx.dormitory.ai.processing.liveness.LivenessStep
import com.ktx.dormitory.ai.processing.liveness.FaceQualityResult
import com.ktx.dormitory.student.face.presentation.components.FaceErrorPopup
import com.ktx.dormitory.student.face.presentation.components.FaceOverlay
import com.ktx.dormitory.core.util.resizeAndCompress
import com.ktx.dormitory.core.util.cropFace
import com.ktx.dormitory.navigation.components.LoadingView
import com.ktx.dormitory.shared.auth.presentation.LoginViewModel
import com.google.mlkit.vision.face.Face
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

data class CaptureState(
    val bitmap: Bitmap? = null,
    val faces: List<Face> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceRegistrationScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    viewModel: FaceRegistrationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val loginState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val livenessState by viewModel.livenessState.collectAsStateWithLifecycle()
    val qualityState by viewModel.qualityState.collectAsStateWithLifecycle()
    val cameraSelector by viewModel.cameraSelector.collectAsStateWithLifecycle()
    val isFlashEnabled by viewModel.isFlashEnabled.collectAsStateWithLifecycle()
    
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var showCamera by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is FaceRegistrationUiEffect.NavigateBack -> navController.popBackStack()
                is FaceRegistrationUiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { 
            cameraExecutor.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đăng ký khuôn mặt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(FaceRegistrationUiEvent.LoadProfile) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                LoadingView()
            } else if (showCamera) {
                CameraView(
                    hasCameraPermission = hasCameraPermission,
                    permissionLauncher = permissionLauncher,
                    cameraExecutor = cameraExecutor,
                    lifecycleOwner = lifecycleOwner,
                    viewModel = viewModel,
                    qualityState = qualityState,
                    livenessState = livenessState,
                    uiState = uiState,
                    cameraSelector = cameraSelector,
                    isFlashEnabled = isFlashEnabled,
                    onCapture = { bitmap, faces ->
                        val student = loginState.userData
                        val face = faces.firstOrNull()
                        if (face == null) {
                            Toast.makeText(context, "Không tìm thấy khuôn mặt trong khung hình. Vui lòng giữ yên máy.", Toast.LENGTH_SHORT).show()
                            return@CameraView
                        }
                        if (student == null) return@CameraView

                        val croppedFace = bitmap.cropFace(face.boundingBox)
                        val finalFace = croppedFace.resizeAndCompress(720)

                        val file = File(context.cacheDir, "temp_face_registration.jpg")
                        val out = FileOutputStream(file)
                        finalFace.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.flush()
                        out.close()

                        if (uiState.faceProfile?.status == "APPROVED") {
                            viewModel.onEvent(FaceRegistrationUiEvent.RequestReplacement(file.absolutePath))
                        } else {
                            viewModel.onEvent(FaceRegistrationUiEvent.RegisterFace(student.fullName ?: "User", file.absolutePath))
                        }

                        if (croppedFace != bitmap) croppedFace.recycle()
                        if (finalFace != croppedFace) finalFace.recycle()
                        showCamera = false
                    },
                    onBack = { showCamera = false }
                )
            } else {
                StatusView(
                    uiState = uiState,
                    onStartRegistration = { 
                        viewModel.onEvent(FaceRegistrationUiEvent.ResetStatus)
                        showCamera = true 
                    }
                )
            }

            if (uiState.errorMessage != null) {
                FaceErrorPopup(
                    errorMessage = uiState.errorMessage!!,
                    onDismiss = { viewModel.onEvent(FaceRegistrationUiEvent.ClearError) }
                )
            }
        }
    }
}

@Composable
fun StatusView(
    uiState: FaceRegistrationUiState,
    onStartRegistration: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val profile = uiState.faceProfile
        
        if (profile == null) {
            // State: 404 - Not Registered
            WelcomeSection(onStartRegistration)
        } else {
            // State: PENDING, APPROVED, REJECTED
            ProfileStatusSection(profile, onStartRegistration)
        }
    }
}

@Composable
fun WelcomeSection(onStartRegistration: () -> Unit) {
    Icon(
        imageVector = Icons.Default.Face,
        contentDescription = null,
        modifier = Modifier.size(120.dp),
        tint = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        "Chào mừng bạn!",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        "Hãy đăng ký khuôn mặt để sử dụng tính năng Smart Access, giúp bạn ra vào ký túc xá một cách thuận tiện và an toàn.",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(48.dp))
    Button(
        onClick = onStartRegistration,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("BẮT ĐẦU ĐĂNG KÝ", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileStatusSection(profile: com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto, onStartRegistration: () -> Unit) {
    val status = profile.status
    val isPendingReplacement = profile.pendingFaceImageUrl != null && status == "APPROVED"
    
    val statusColor = if (isPendingReplacement) {
        Color(0xFFFFC107) // Yellow for pending replacement
    } else {
        when (status) {
            "APPROVED" -> Color(0xFF4CAF50)
            "PENDING" -> Color(0xFFFFC107)
            "REJECTED" -> Color(0xFFF44336)
            else -> Color.Gray
        }
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        AsyncImage(
            model = profile.faceImageUrl,
            contentDescription = "Face Image",
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(4.dp, statusColor, CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .then(if (status == "PENDING" || isPendingReplacement) Modifier.alpha(0.6f) else Modifier),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = when {
                    isPendingReplacement -> Icons.Default.Timer
                    status == "APPROVED" -> Icons.Default.CheckCircle
                    status == "PENDING" -> Icons.Default.Timer
                    status == "REJECTED" -> Icons.Default.Cancel
                    else -> Icons.Default.Help
                },
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = when {
            isPendingReplacement -> "Yêu cầu đổi ảnh đang chờ duyệt"
            status == "APPROVED" -> "Ảnh hợp lệ - Đang sử dụng"
            status == "PENDING" -> "Đang chờ Ban Quản Lý duyệt"
            status == "REJECTED" -> "Yêu cầu bị từ chối"
            else -> "Trạng thái không xác định"
        },
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = statusColor,
        textAlign = TextAlign.Center
    )

    if (status == "REJECTED" && profile.rejectionReason != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Lý do: ${profile.rejectionReason}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    if (status == "PENDING" || status == "REJECTED") {
        Button(
            onClick = onStartRegistration,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (status == "REJECTED") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (status == "PENDING") "CHỤP LẠI ẢNH KHÁC" else "CHỤP LẠI ẢNH CHUẨN", fontWeight = FontWeight.Bold)
        }
    } else if (status == "APPROVED") {
        if (!isPendingReplacement) {
            Button(
                onClick = onStartRegistration,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("CẬP NHẬT ẢNH MỚI", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = false
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ĐÃ KÍCH HOẠT SMART ACCESS", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CameraView(
    hasCameraPermission: Boolean,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    cameraExecutor: java.util.concurrent.ExecutorService,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    viewModel: FaceRegistrationViewModel,
    qualityState: FaceQualityResult,
    livenessState: FaceLivenessUiState,
    uiState: FaceRegistrationUiState,
    cameraSelector: CameraSelector,
    isFlashEnabled: Boolean,
    onCapture: (Bitmap, List<Face>) -> Unit,
    onBack: () -> Unit
) {
    var captureState by remember { mutableStateOf(CaptureState()) }
    val isRegistering = uiState.isRegistering
    val context = LocalContext.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            key(cameraSelector, isFlashEnabled) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        }
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            
                            val resolutionSelector = ResolutionSelector.Builder()
                                .setResolutionStrategy(
                                    ResolutionStrategy(
                                        Size(1280, 720),
                                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER
                                    )
                                )
                                .build()

                            val preview = Preview.Builder()
                                .setResolutionSelector(resolutionSelector)
                                .build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setResolutionSelector(resolutionSelector)
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(cameraExecutor, FaceAnalyzer(viewModel) { faces, _, _, bitmap ->
                                        // Fix: Chỉ cập nhật frame mới khi chưa hoàn thành liveness hoặc chưa có bitmap
                                        // Khi đã hoàn thành, ta giữ lại frame cuối cùng để tránh lỗi mất khuôn mặt khi rung tay
                                        if (livenessState.currentStep != LivenessStep.COMPLETED || captureState.bitmap == null) {
                                            captureState.bitmap?.takeIf { old -> old != bitmap && !old.isRecycled }?.recycle()
                                            captureState = CaptureState(bitmap, faces)
                                        }
                                    })
                                }
                            
                            try {
                                cameraProvider.unbindAll()
                                val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                                
                                // Kiểm tra xem camera hiện tại có đèn Flash không trước khi bật
                                if (camera.cameraInfo.hasFlashUnit()) {
                                    camera.cameraControl.enableTorch(isFlashEnabled)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // eKYC Overlay
            FaceOverlay(
                isFaceDetected = captureState.faces.isNotEmpty(),
                facePositionOk = uiState.facePositionOk
            )

            // Screen Flash Effect for Front Camera
            if (isFlashEnabled && cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.3f)) // Màn hình sáng mờ để hỗ trợ ánh sáng
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Cần quyền Camera để đăng ký khuôn mặt", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("CẤP QUYỀN CAMERA")
                }
            }
        }

        // Overlay & HUD
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(16.dp).fillMaxWidth()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (qualityState.isGood) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (qualityState.isGood) {
                            if (livenessState.lookBackRequired) "Đã nhận! Hãy nhìn thẳng lại camera"
                            else "Bước: ${getStepName(livenessState.currentStep)}"
                        } else qualityState.message,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (qualityState.message.contains("Vui lòng") || !qualityState.isGood) {
                        Text(
                            text = "Đưa khuôn mặt vào trong khung hình, nhìn thẳng, đảm bảo đủ ánh sáng và không đeo kính râm hay khẩu trang",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (qualityState.isGood) {
                        LinearProgressIndicator(
                            progress = { livenessState.progress },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
        }

        // Camera Controls (Flip & Flash)
        Row(
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            IconButton(
                onClick = { viewModel.toggleFlash() },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFlashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (isFlashEnabled) Color.Yellow else Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { viewModel.toggleCamera() },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip Camera", tint = Color.White)
            }
        }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isCompleted = livenessState.currentStep == LivenessStep.COMPLETED
                    
                    Button(
                        onClick = {
                            val currentFrame = captureState
                            val bitmap = currentFrame.bitmap
                            val faces = currentFrame.faces
                            
                            if (bitmap != null) {
                                // Nếu liveness đã xong nhưng faces rỗng (do cử động nhanh), 
                                // ta vẫn cho phép capture nếu bitmap tồn tại.
                                onCapture(bitmap, faces.ifEmpty { 
                                    // Tạo face giả lập từ bounding box trung tâm nếu cần, 
                                    // hoặc thông báo người dùng giữ yên.
                                    emptyList() 
                                })
                            } else {
                                Toast.makeText(context, "Đang chuẩn bị ảnh, vui lòng giữ yên máy trong giây lát.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !isRegistering && isCompleted
                    ) {
                        if (isRegistering) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (isCompleted) "LƯU KHUÔN MẶT" else "HOÀN THÀNH CÁC BƯỚC TRÊN")
                        }
                    }
                }
    }
}

fun getStepName(step: LivenessStep): String {
    return when (step) {
        LivenessStep.EYE_BLINK -> "Hãy nháy mắt"
        LivenessStep.TURN_LEFT -> "Hãy quay đầu sang trái"
        LivenessStep.TURN_RIGHT -> "Hãy quay đầu sang phải"
        LivenessStep.SMILE -> "Hãy mỉm cười"
        LivenessStep.COMPLETED -> "Xác thực người thật thành công"
    }
}

@ComposePreview(showBackground = true)
@Composable
fun FaceRegistrationPreview_Welcome() {
    MaterialTheme {
        StatusView(
            uiState = FaceRegistrationUiState(isLoading = false, faceProfile = null),
            onStartRegistration = {}
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun FaceRegistrationPreview_Pending() {
    MaterialTheme {
        StatusView(
            uiState = FaceRegistrationUiState(
                isLoading = false,
                faceProfile = com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto(
                    profileId = java.util.UUID.randomUUID(),
                    studentId = java.util.UUID.randomUUID(),
                    faceImageUrl = "https://res.cloudinary.com/dpds3gjbj/image/upload/v1783389864/sdms/faces/vh7x80hwkpf1c7vg7cjh.jpg",
                    status = "PENDING",
                    rejectionReason = null,
                    pendingFaceImageUrl = null,
                    replacementRequestedAt = null,
                    createdAt = "2026-07-07T09:04:24.787"
                )
            ),
            onStartRegistration = {}
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun FaceRegistrationPreview_Rejected() {
    MaterialTheme {
        StatusView(
            uiState = FaceRegistrationUiState(
                isLoading = false,
                faceProfile = com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto(
                    profileId = java.util.UUID.randomUUID(),
                    studentId = java.util.UUID.randomUUID(),
                    faceImageUrl = "https://res.cloudinary.com/dpds3gjbj/image/upload/v1783389864/sdms/faces/vh7x80hwkpf1c7vg7cjh.jpg",
                    status = "REJECTED",
                    rejectionReason = "Ảnh quá mờ, không thấy rõ ngũ quan",
                    pendingFaceImageUrl = null,
                    replacementRequestedAt = null,
                    createdAt = "2026-07-07T09:04:24.787"
                )
            ),
            onStartRegistration = {}
        )
    }
}

@ComposePreview(showBackground = true)
@Composable
fun FaceRegistrationPreview_ReplacementPending() {
    MaterialTheme {
        StatusView(
            uiState = FaceRegistrationUiState(
                isLoading = false,
                faceProfile = com.ktx.dormitory.student.face.data.dto.response.FaceProfileDto(
                    profileId = java.util.UUID.randomUUID(),
                    studentId = java.util.UUID.randomUUID(),
                    faceImageUrl = "https://res.cloudinary.com/dpds3gjbj/image/upload/v1783389864/sdms/faces/vh7x80hwkpf1c7vg7cjh.jpg",
                    status = "APPROVED",
                    rejectionReason = null,
                    pendingFaceImageUrl = "https://res.cloudinary.com/dpds3gjbj/image/upload/v1/temp_pending.jpg",
                    replacementRequestedAt = "2026-07-07T10:00:00.000",
                    createdAt = "2026-07-07T09:04:24.787"
                )
            ),
            onStartRegistration = {}
        )
    }
}

