package com.ktx.dormitory.di.feature

import com.ktx.dormitory.shared.profile.data.remote.ProfileApiService
import com.ktx.dormitory.shared.profile.data.remote.ProfileRemoteDataSource
import com.ktx.dormitory.shared.profile.data.remote.ProfileRemoteDataSourceImpl
import com.ktx.dormitory.shared.profile.data.local.ProfileLocalDataSource
import com.ktx.dormitory.shared.profile.data.local.ProfileLocalDataSourceImpl
import com.ktx.dormitory.shared.profile.data.repository.ProfileRepositoryImpl
import com.ktx.dormitory.shared.profile.domain.repository.ProfileRepository

import com.ktx.dormitory.student.room.data.remote.RoomApiService
import com.ktx.dormitory.student.room.data.remote.RoomRemoteDataSource
import com.ktx.dormitory.student.room.data.remote.RoomRemoteDataSourceImpl
import com.ktx.dormitory.student.room.data.repository.RoomRepositoryImpl
import com.ktx.dormitory.student.room.domain.repository.RoomRepository

import com.ktx.dormitory.shared.notification.data.remote.NotificationApiService
import com.ktx.dormitory.shared.notification.data.repository.NotificationRepositoryImpl
import com.ktx.dormitory.shared.notification.domain.repository.NotificationRepository

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
abstract class CommonFeatureModule {

    // Profile
    @Binds
    @Singleton
    abstract fun bindProfileRemoteDataSource(impl: ProfileRemoteDataSourceImpl): ProfileRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindProfileLocalDataSource(impl: ProfileLocalDataSourceImpl): ProfileLocalDataSource

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    // Room
    @Binds
    @Singleton
    abstract fun bindRoomRemoteDataSource(impl: RoomRemoteDataSourceImpl): RoomRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindRoomRepository(impl: RoomRepositoryImpl): RoomRepository

    // Notification
    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    // Maintenance
    @Binds
    @Singleton
    abstract fun bindMaintenanceRepository(impl: MaintenanceRepositoryImpl): MaintenanceRepository

    companion object {
        @Provides
        @Singleton
        fun provideProfileApi(retrofit: Retrofit): ProfileApiService = retrofit.create(ProfileApiService::class.java)

        @Provides
        @Singleton
        fun provideRoomApi(retrofit: Retrofit): RoomApiService = retrofit.create(RoomApiService::class.java)

        @Provides
        @Singleton
        fun provideNotificationApi(retrofit: Retrofit): NotificationApiService = retrofit.create(NotificationApiService::class.java)

        @Provides
        @Singleton
        fun provideMaintenanceApi(retrofit: Retrofit): MaintenanceApiService = retrofit.create(MaintenanceApiService::class.java)
    }
}
