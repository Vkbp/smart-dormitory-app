package com.ktx.dormitory.domain.auth.usecase

import com.google.common.truth.Truth.assertThat
import com.ktx.dormitory.domain.auth.model.UserData
import com.ktx.dormitory.domain.auth.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var loginUseCase: LoginUseCase
    private val authRepository: AuthRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `invoke with valid credentials returns success and saves login status`() = runTest {
        // Arrange
        val userData = UserData(id = "1", username = "test_user", role = "STUDENT")
        coEvery { authRepository.login("test_user", "password") } returns Result.success(userData)

        // Act
        val result = loginUseCase("test_user", "password")

        // Assert
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(userData)
        coVerify { authRepository.saveLoginStatus(true) }
    }

    @Test
    fun `invoke with invalid credentials returns failure and does not save login status`() = runTest {
        // Arrange
        coEvery { authRepository.login("test_user", "wrong_pass") } returns Result.failure(Exception("Login failed"))

        // Act
        val result = loginUseCase("test_user", "wrong_pass")

        // Assert
        assertThat(result.isFailure).isTrue()
        coVerify(exactly = 0) { authRepository.saveLoginStatus(any()) }
    }
}
