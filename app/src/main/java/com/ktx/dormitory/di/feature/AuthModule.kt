package com.ktx.dormitory.di.feature

import com.ktx.dormitory.shared.auth.data.local.AuthLocalDataSource
import com.ktx.dormitory.shared.auth.data.local.TokenManager
import com.ktx.dormitory.shared.auth.data.remote.AuthApiService
import com.ktx.dormitory.shared.auth.data.remote.AuthRemoteDataSource
import com.ktx.dormitory.shared.auth.data.remote.AuthRemoteDataSourceImpl
import com.ktx.dormitory.shared.auth.data.repository.AuthRepositoryImpl
import com.ktx.dormitory.shared.auth.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        authRemoteDataSourceImpl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthLocalDataSource(
        tokenManager: TokenManager
    ): AuthLocalDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
            return retrofit.create(AuthApiService::class.java)
        }
    }
}
