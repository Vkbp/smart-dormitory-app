package com.ktx.dormitory.student.payment.data.remote

import com.google.gson.annotations.SerializedName
import com.ktx.dormitory.core.common.BaseResponse
import com.ktx.dormitory.core.common.PageResponse
import com.ktx.dormitory.student.payment.data.dto.request.OnlinePaymentRequestDto
import com.ktx.dormitory.student.payment.data.dto.response.BillDto
import com.ktx.dormitory.student.payment.data.dto.response.PaymentResponseDto
import com.ktx.dormitory.student.payment.data.dto.response.PaymentInstructionDto
import retrofit2.http.*

interface PaymentApiService {
    
    /**
     * Lấy thông tin hóa đơn dựa trên Application ID (hồ sơ đăng ký).
     * Dùng cho sinh viên mới chưa có tài khoản hoặc đang trong quá trình check-in.
     */
    @GET("v1/bills/application/{applicationId}")
    suspend fun getBillByApplication(
        @Path("applicationId") applicationId: String
    ): BaseResponse<BillDto>

    /**
     * Lấy danh sách hóa đơn của chính mình (đã login).
     */
    @GET("v1/bills/me")
    suspend fun getInvoices(): BaseResponse<List<BillDto>>

    /**
     * Tạo mã QR thông minh (Smart QR) để thanh toán online qua SePay.
     */
    @POST("v1/payments/online")
    suspend fun createSmartQR(
        @Body request: OnlinePaymentRequestDto
    ): BaseResponse<PaymentResponseDto>

    /**
     * Lấy lịch sử hóa đơn phân trang.
     */
    @GET("v1/bills/me/paged")
    suspend fun getPaymentHistoryPaged(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "dueDate,desc"
    ): BaseResponse<PageResponse<BillDto>>

    /**
     * Lấy hướng dẫn thanh toán (Static QR hoặc Dynamic QR cho hóa đơn cụ thể).
     * Endpoint công khai, không cần token.
     */
    @GET("v1/public/payment-instructions")
    suspend fun getPaymentInstructions(
        @Query("billId") billId: String?
    ): BaseResponse<PaymentInstructionDto>

    /**
     * Tách nợ tiền điện cho các thành viên không đóng tiền.
     */
    @POST("v1/bills/{billId}/split")
    suspend fun splitElectricBill(
        @Path("billId") billId: String,
        @Body request: SplitBillRequest
    ): BaseResponse<Unit>
}

data class SplitBillRequest(
    @SerializedName("nonPayingStudentIds") val nonPayingStudentIds: List<String>,
    @SerializedName("amountPerStudent") val amountPerStudent: java.math.BigDecimal
)
