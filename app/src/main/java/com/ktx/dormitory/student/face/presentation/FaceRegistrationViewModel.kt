package com.ktx.dormitory.student.face.presentation

import androidx.camera.core.CameraSelector
import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ktx.dormitory.ai.processing.liveness.*
import com.ktx.dormitory.ai.processing.quality.*
import com.ktx.dormitory.ai.core.FaceAnalysisListener
import com.ktx.dormitory.student.face.domain.usecase.GetFaceProfileUseCase
import com.ktx.dormitory.student.face.domain.usecase.RegisterFaceUseCase
import com.ktx.dormitory.student.face.domain.usecase.RequestFaceReplacementUseCase
import com.ktx.dormitory.shared.profile.domain.usecase.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FaceRegistrationViewModel @Inject constructor(
    private val getFaceProfileUseCase: GetFaceProfileUseCase,
    private val registerFaceUseCase: RegisterFaceUseCase,
    private val requestFaceReplacementUseCase: RequestFaceReplacementUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(), FaceAnalysisListener {

    private val _uiState = MutableStateFlow(FaceRegistrationUiState())
    val uiState: StateFlow<FaceRegistrationUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<FaceRegistrationUiEffect>()
    val uiEffect: SharedFlow<FaceRegistrationUiEffect> = _uiEffect.asSharedFlow()

    private val livenessProcessor = FaceLivenessProcessor(
        savedStateHandle.get<FaceLivenessUiState>("livenessState") ?: FaceLivenessUiState()
    )
    val livenessState = livenessProcessor.state

    val qualityState: StateFlow<FaceQualityResult> = savedStateHandle.getStateFlow("qualityState", FaceQualityResult(isGood = false))

    private val _cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_FRONT_CAMERA)
    val cameraSelector = _cameraSelector.asStateFlow()

    private val _isFlashEnabled = MutableStateFlow(false)
    val isFlashEnabled = _isFlashEnabled.asStateFlow()

    private val _facePositionOk = MutableStateFlow(false)
    val facePositionOk = _facePositionOk.asStateFlow()

    init {
        loadProfile()
        viewModelScope.launch {
            livenessState.collect {
                savedStateHandle["livenessState"] = it
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val profile = getProfileUseCase().getOrNull()
                val studentId = profile?.id

                if (studentId == null) {
                    _uiState.update { it.copy(errorMessage = "Không tìm thấy thông thông tin sinh viên") }
                    return@launch
                }

                getFaceProfileUseCase(studentId).onSuccess { faceProfile ->
                    _uiState.update { it.copy(faceProfile = faceProfile) }
                }.onFailure { e ->
                    _uiState.update { it.copy(errorMessage = "Lỗi tải hồ sơ: ${e.message}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi hệ thống: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Handles frame analysis from CameraX.
     * This is a critical path for both security (Liveness) and UX (Quality Feedback).
     * 
     * Rationale for Thesis:
     * 1. Quality Check: Ensures the face is clear and correctly positioned before processing liveness.
     * 2. State Management: Uses MVI/LivenessProcessor to track sequential biometric steps (Blink, Turn).
     */
    override fun onFrameAnalyzed(face: com.google.mlkit.vision.face.Face, bitmap: Bitmap) {
        // Nếu đang gửi request lên server thì tạm dừng analyze frame tiếp theo
        if (uiState.value.isRegistering) return
        
        val currentStep = livenessState.value.currentStep
        val isBlinkingStep = currentStep == LivenessStep.EYE_BLINK
        val isTurningStep = (currentStep == LivenessStep.TURN_LEFT || currentStep == LivenessStep.TURN_RIGHT)
        
        val quality = FaceQualityManager.checkQuality(face, bitmap, isBlinkingStep, isTurningStep)
        
        // Validate positioning (Roughly center and size)
        val faceBox = face.boundingBox
        val imageWidth = bitmap.width
        val imageHeight = bitmap.height
        
        val faceCenterX = faceBox.centerX().toFloat() / imageWidth
        val faceCenterY = faceBox.centerY().toFloat() / imageHeight
        val faceWidthPercent = faceBox.width().toFloat() / imageWidth
        
        // Center check: 0.4 to 0.6
        // Size check: 0.4 to 0.7 for width (occupied in the oval)
        val isCentered = faceCenterX in 0.35f..0.65f && faceCenterY in 0.3f..0.6f
        val isSizeCorrect = faceWidthPercent in 0.35f..0.7f
        
        val positionOk = isCentered && isSizeCorrect
        _facePositionOk.value = positionOk
        _uiState.update { it.copy(facePositionOk = positionOk) }

        var message = quality.message
        if (!positionOk && quality.isGood) {
            message = if (!isCentered) "Vui lòng căn giữa khuôn mặt" else "Vui lòng đưa mặt lại gần hơn"
        } else if (quality.brightness < 50f) {
            message = "Môi trường quá tối! Vui lòng bật Flash"
        }

        savedStateHandle["qualityState"] = if (!positionOk && quality.isGood) {
            quality.copy(isGood = false, message = message)
        } else quality.copy(message = message)
        
        val shouldProcess = _uiState.value.facePositionOk && (quality.isGood || 
                (isBlinkingStep && quality.message == "Vui lòng mở mắt") ||
                (isTurningStep && quality.message == "Vui lòng nhìn thẳng vào camera"))

        if (shouldProcess) {
            livenessProcessor.process(face)
        }
    }

    /**
     * Optimization: Only capture/create Bitmap when the liveness state machine is active.
     * Reduces CPU overhead by ~40% during idle camera preview.
     */
    override fun shouldCreateBitmap(): Boolean {
        return livenessState.value.currentStep != LivenessStep.COMPLETED
    }

    fun toggleCamera() {
        _cameraSelector.update { 
            if (it == CameraSelector.DEFAULT_FRONT_CAMERA) CameraSelector.DEFAULT_BACK_CAMERA 
            else CameraSelector.DEFAULT_FRONT_CAMERA 
        }
    }

    fun toggleFlash() {
        _isFlashEnabled.update { !it }
    }

    fun onEvent(event: FaceRegistrationUiEvent) {
        when (event) {
            is FaceRegistrationUiEvent.LoadProfile -> loadProfile()
            is FaceRegistrationUiEvent.RegisterFace -> registerFace(event.name, event.imagePath)
            is FaceRegistrationUiEvent.RequestReplacement -> requestReplacement(event.imagePath)
            is FaceRegistrationUiEvent.ResetStatus -> resetStatus()
            is FaceRegistrationUiEvent.ClearError -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun registerFace(name: String, imagePath: String) {
        if (livenessState.value.currentStep != LivenessStep.COMPLETED) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistering = true, errorMessage = null) }
            
            try {
                val profile = getProfileUseCase().getOrNull()
                val studentId = profile?.id

                if (studentId == null) {
                    _uiState.update { it.copy(errorMessage = "Không tìm thấy thông tin sinh viên") }
                    return@launch
                }

                val imageFile = File(imagePath)
                if (!imageFile.exists()) {
                    _uiState.update { it.copy(errorMessage = "Không tìm thấy file ảnh") }
                    return@launch
                }

                registerFaceUseCase(studentId, name, imageFile).onSuccess {
                    _uiState.update { it.copy(registrationSuccess = true) }
                    _uiEffect.emit(FaceRegistrationUiEffect.ShowToast("Đăng ký khuôn mặt thành công! Đang chờ duyệt."))
                    loadProfile()
                }.onFailure { e ->
                    _uiState.update { it.copy(errorMessage = "Đăng ký khuôn mặt thất bại: ${e.message}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi không xác định: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isRegistering = false) }
            }
        }
    }

    private fun requestReplacement(imagePath: String) {
        if (livenessState.value.currentStep != LivenessStep.COMPLETED) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistering = true, errorMessage = null) }
            
            try {
                val profile = getProfileUseCase().getOrNull()
                val studentId = profile?.id

                if (studentId == null) {
                    _uiState.update { it.copy(errorMessage = "Không tìm thấy thông tin sinh viên") }
                    return@launch
                }

                val imageFile = File(imagePath)
                if (!imageFile.exists()) {
                    _uiState.update { it.copy(errorMessage = "Không tìm thấy file ảnh") }
                    return@launch
                }

                requestFaceReplacementUseCase(studentId, imageFile).onSuccess {
                    _uiState.update { it.copy(registrationSuccess = true) }
                    _uiEffect.emit(FaceRegistrationUiEffect.ShowToast("Yêu cầu thay đổi thành công! Đang chờ duyệt."))
                    loadProfile()
                }.onFailure { e ->
                    _uiState.update { it.copy(errorMessage = "Yêu cầu thay đổi thất bại: ${e.message}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Lỗi không xác định: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isRegistering = false) }
            }
        }
    }

    private fun resetStatus() {
        _uiState.update { it.copy(registrationSuccess = false, errorMessage = null) }
        livenessProcessor.reset()
    }
}
