package com.ktx.dormitory.di.feature

import com.ktx.dormitory.shared.notification.data.remote.NotificationApiService
import com.ktx.dormitory.shared.notification.data.repository.NotificationRepositoryImpl
import com.ktx.dormitory.shared.notification.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    companion object {
        @Provides
        @Singleton
        fun provideNotificationApi(retrofit: Retrofit): NotificationApiService = retrofit.create(NotificationApiService::class.java)
    }
}
