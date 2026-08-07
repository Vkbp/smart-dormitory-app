package com.ktx.dormitory.admin.dashboard.domain.usecase

import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(private val repository: AdminRepository) {
    suspend operator fun invoke() = repository.getDashboardStats()
}
