package com.ktx.dormitory.student.face.data.dto.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class FaceProfileDto(
    @SerializedName("profileId", alternate = ["id"]) val profileId: UUID?,
    @SerializedName("studentId") val studentId: UUID?,
    @SerializedName("studentCode", alternate = ["student_code"]) val studentCode: String? = null,
    @SerializedName("fullName", alternate = ["full_name"]) val fullName: String? = null,
    @SerializedName("faceImageUrl") val faceImageUrl: String?,
    @SerializedName("status") val status: String,
    @SerializedName("rejectionReason") val rejectionReason: String?,
    @SerializedName("pendingFaceImageUrl") val pendingFaceImageUrl: String?,
    @SerializedName("replacementRequestedAt") val replacementRequestedAt: String?,
    @SerializedName("createdAt") val createdAt: String?
) : Parcelable
