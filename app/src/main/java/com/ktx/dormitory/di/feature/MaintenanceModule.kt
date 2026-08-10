package com.ktx.dormitory.di.feature

import com.ktx.dormitory.student.maintenance.data.remote.MaintenanceApiService
import com.ktx.dormitory.student.maintenance.data.repository.MaintenanceRepositoryImpl
import com.ktx.dormitory.student.maintenance.domain.repository.MaintenanceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MaintenanceModule {

    @Binds
    @Singleton
    abstract fun bindMaintenanceRepository(impl: MaintenanceRepositoryImpl): MaintenanceRepository

    companion object {
        @Provides
        @Singleton
        fun provideMaintenanceApi(retrofit: Retrofit): MaintenanceApiService = retrofit.create(MaintenanceApiService::class.java)
    }
}
