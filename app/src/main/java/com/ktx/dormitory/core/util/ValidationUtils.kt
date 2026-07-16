package com.ktx.dormitory.core.util

/**
 * Utility class for client-side validation, ensuring parity with Backend rules.
 * Reference: docs/business/VALIDATION_SPECIFICATION.md
 */
object ValidationUtils {

    /**
     * Password Complexity (BR-A02)
     * - Length: 8-50 characters.
     * - At least 1 lowercase letter.
     * - At least 1 uppercase letter.
     * - At least 1 digit.
     * - At least 1 special character (@#$%^&+=!).
     * - No whitespace.
     */
    private const val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,50}$"

    fun isValidPassword(password: String): Boolean {
        return password.matches(Regex(PASSWORD_REGEX))
    }

    /**
     * Vietnamese CCCD (12 digits)
     */
    fun isValidCCCD(cccd: String): Boolean {
        return cccd.matches(Regex("^\\d{12}$"))
    }

    /**
     * Vietnamese Phone Number (10 digits starting with 0)
     */
    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^0\\d{9}$"))
    }

    /**
     * Standard Email format
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        return email.matches(Regex(emailRegex))
    }
}
