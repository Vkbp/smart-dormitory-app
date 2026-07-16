package com.ktx.dormitory.di.feature

import com.ktx.dormitory.student.payment.data.remote.PaymentApiService
import com.ktx.dormitory.student.payment.data.remote.PaymentRemoteDataSource
import com.ktx.dormitory.student.payment.data.remote.PaymentRemoteDataSourceImpl
import com.ktx.dormitory.student.payment.data.repository.PaymentRepositoryImpl
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository

import com.ktx.dormitory.student.access.data.remote.AccessApiService
import com.ktx.dormitory.student.access.data.remote.AccessRemoteDataSource
import com.ktx.dormitory.student.access.data.remote.AccessRemoteDataSourceImpl
import com.ktx.dormitory.student.access.data.repository.AccessRepositoryImpl
import com.ktx.dormitory.student.access.domain.repository.AccessRepository

import com.ktx.dormitory.student.face.data.remote.FaceApiService
import com.ktx.dormitory.student.face.data.remote.FaceRemoteDataSource
import com.ktx.dormitory.student.face.data.remote.FaceRemoteDataSourceImpl
import com.ktx.dormitory.student.face.data.repository.FaceRepositoryImpl
import com.ktx.dormitory.student.face.domain.repository.FaceRepository

import com.ktx.dormitory.data.settings.repository.SettingsRepositoryImpl
import com.ktx.dormitory.domain.settings.repository.SettingsRepository

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilityModule {

    // Payment
    @Binds
    @Singleton
    abstract fun bindPaymentRemoteDataSource(impl: PaymentRemoteDataSourceImpl): PaymentRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    // Access
    @Binds
    @Singleton
    abstract fun bindAccessRemoteDataSource(impl: AccessRemoteDataSourceImpl): AccessRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAccessRepository(impl: AccessRepositoryImpl): AccessRepository

    // Face
    @Binds
    @Singleton
    abstract fun bindFaceRemoteDataSource(impl: FaceRemoteDataSourceImpl): FaceRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFaceRepository(impl: FaceRepositoryImpl): FaceRepository

    // Settings
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun providePaymentApi(retrofit: Retrofit): PaymentApiService = retrofit.create(PaymentApiService::class.java)

        @Provides
        @Singleton
        fun provideAccessApi(retrofit: Retrofit): AccessApiService = retrofit.create(AccessApiService::class.java)

        @Provides
        @Singleton
        fun provideFaceApi(retrofit: Retrofit): FaceApiService = retrofit.create(FaceApiService::class.java)
    }
}
