package com.ktx.dormitory.student.checkout.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateCheckoutRequestDto(
    @SerializedName("intendedCheckoutDate") val intendedCheckoutDate: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("bankAccountNumber") val bankAccountNumber: String,
    @SerializedName("bankName") val bankName: String
)
