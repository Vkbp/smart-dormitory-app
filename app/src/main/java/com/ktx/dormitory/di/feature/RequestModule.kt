package com.ktx.dormitory.di.feature

import com.ktx.dormitory.student.checkout.data.remote.CheckoutApiService
import com.ktx.dormitory.student.checkout.data.repository.CheckoutRepositoryImpl
import com.ktx.dormitory.student.checkout.domain.repository.CheckoutRepository

import com.ktx.dormitory.student.extension.data.remote.ExtensionApiService
import com.ktx.dormitory.student.extension.data.repository.ExtensionRepositoryImpl
import com.ktx.dormitory.student.extension.domain.repository.ExtensionRepository

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RequestModule {

    @Binds
    @Singleton
    abstract fun bindCheckoutRepository(impl: CheckoutRepositoryImpl): CheckoutRepository

    @Binds
    @Singleton
    abstract fun bindExtensionRepository(impl: ExtensionRepositoryImpl): ExtensionRepository

    companion object {
        @Provides
        @Singleton
        fun provideCheckoutApi(retrofit: Retrofit): CheckoutApiService = retrofit.create(CheckoutApiService::class.java)

        @Provides
        @Singleton
        fun provideExtensionApi(retrofit: Retrofit): ExtensionApiService = retrofit.create(ExtensionApiService::class.java)
    }
}
