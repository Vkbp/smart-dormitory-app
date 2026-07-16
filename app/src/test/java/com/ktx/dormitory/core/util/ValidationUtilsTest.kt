package com.ktx.dormitory.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun `isValidPassword should return true for valid passwords`() {
        assertTrue(ValidationUtils.isValidPassword("StrongPass123!"))
        assertTrue(ValidationUtils.isValidPassword("Abc@1234"))
    }

    @Test
    fun `isValidPassword should return false for invalid passwords`() {
        assertFalse(ValidationUtils.isValidPassword("weak")) // Too short
        assertFalse(ValidationUtils.isValidPassword("nouppercase123!")) // No uppercase
        assertFalse(ValidationUtils.isValidPassword("NOLOWERCASE123!")) // No lowercase
        assertFalse(ValidationUtils.isValidPassword("NoDigitSpecial")) // No digit/special
        assertFalse(ValidationUtils.isValidPassword("Space Password1!")) // Contains space
    }

    @Test
    fun `isValidCCCD should validate 12 digit format`() {
        assertTrue(ValidationUtils.isValidCCCD("123456789012"))
        assertFalse(ValidationUtils.isValidCCCD("12345678901")) // 11 digits
        assertFalse(ValidationUtils.isValidCCCD("1234567890123")) // 13 digits
        assertFalse(ValidationUtils.isValidCCCD("12345678901A")) // Non-digit
    }

    @Test
    fun `isValidPhone should validate Vietnamese phone format`() {
        assertTrue(ValidationUtils.isValidPhone("0912345678"))
        assertFalse(ValidationUtils.isValidPhone("1912345678")) // Doesn't start with 0
        assertFalse(ValidationUtils.isValidPhone("091234567")) // 9 digits
        assertFalse(ValidationUtils.isValidPhone("09123456789")) // 11 digits
    }

    @Test
    fun `isValidEmail should validate email format`() {
        assertTrue(ValidationUtils.isValidEmail("test@gmail.com"))
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"))
        assertFalse(ValidationUtils.isValidEmail("invalid-email"))
        assertFalse(ValidationUtils.isValidEmail("test@domain"))
    }
}
