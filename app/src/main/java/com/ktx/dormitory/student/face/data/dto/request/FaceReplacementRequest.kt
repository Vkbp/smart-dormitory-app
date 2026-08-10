package com.ktx.dormitory.student.face.data.dto.request

import com.google.gson.annotations.SerializedName

data class FaceReplacementRequest(
    @SerializedName("pendingFaceImageUrl") val pendingFaceImageUrl: String
)
