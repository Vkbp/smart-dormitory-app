package com.ktx.dormitory.core.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DateTimeUtilsTest {

    @Test
    fun `formatIsoDate with valid input returns display format`() {
        val input = "2024-06-15"
        val expected = "15/06/2024"
        val result = DateTimeUtils.formatIsoDate(input)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formatIsoDate with null input returns NA`() {
        val result = DateTimeUtils.formatIsoDate(null)
        assertThat(result).isEqualTo("N/A")
    }

    @Test
    fun `formatIsoDate with empty input returns NA`() {
        val result = DateTimeUtils.formatIsoDate("")
        assertThat(result).isEqualTo("N/A")
    }

    @Test
    fun `formatIsoDate with invalid input returns original string`() {
        val input = "invalid-date"
        val result = DateTimeUtils.formatIsoDate(input)
        assertThat(result).isEqualTo(input)
    }

    @Test
    fun `formatIsoDateTime with valid input returns display format`() {
        val input = "2024-06-15T14:30:00"
        val expected = "15/06/2024 14:30"
        val result = DateTimeUtils.formatIsoDateTime(input)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `formatIsoDateTime with null input returns NA`() {
        val result = DateTimeUtils.formatIsoDateTime(null)
        assertThat(result).isEqualTo("N/A")
    }

    @Test
    fun `formatIsoDateTime with date-only input returns date format`() {
        val input = "2024-06-15"
        val expected = "15/06/2024"
        val result = DateTimeUtils.formatIsoDateTime(input)
        assertThat(result).isEqualTo(expected)
    }
}
