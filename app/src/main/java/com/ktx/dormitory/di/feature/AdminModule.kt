package com.ktx.dormitory.di.feature

import com.ktx.dormitory.admin.common.data.remote.AdminApiService
import com.ktx.dormitory.admin.common.data.repository.AdminRepositoryImpl
import com.ktx.dormitory.admin.smartaccess.domain.repository.AdminRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        adminRepositoryImpl: AdminRepositoryImpl
    ): AdminRepository

    companion object {
        @Provides
        @Singleton
        fun provideAdminApiService(retrofit: Retrofit): AdminApiService {
            return retrofit.create(AdminApiService::class.java)
        }
    }
}
