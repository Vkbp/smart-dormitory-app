package com.ktx.dormitory.student.maintenance.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateMaintenanceRequest(
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imageUrl: String? = null
)
