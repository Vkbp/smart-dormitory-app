package com.ktx.dormitory.core.util

import java.util.UUID

object IdempotencyUtils {
    fun generateKey(): String = UUID.randomUUID().toString()
}
