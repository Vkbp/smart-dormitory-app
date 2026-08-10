package com.ktx.dormitory.di.feature

import com.ktx.dormitory.student.payment.data.remote.PaymentApiService
import com.ktx.dormitory.student.payment.data.remote.PaymentRemoteDataSource
import com.ktx.dormitory.student.payment.data.remote.PaymentRemoteDataSourceImpl
import com.ktx.dormitory.student.payment.data.repository.PaymentRepositoryImpl
import com.ktx.dormitory.student.payment.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRemoteDataSource(impl: PaymentRemoteDataSourceImpl): PaymentRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    companion object {
        @Provides
        @Singleton
        fun providePaymentApi(retrofit: Retrofit): PaymentApiService = retrofit.create(PaymentApiService::class.java)
    }
}
