package com.ktx.dormitory.core.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

object CurrencyUtils {
    fun formatCurrency(amount: BigDecimal?): String {
        if (amount == null) return "0 VNĐ"
        val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return format.format(amount)
    }
}
