package com.ktx.dormitory.di.feature

import com.ktx.dormitory.student.access.data.remote.AccessApiService
import com.ktx.dormitory.student.access.data.remote.AccessRemoteDataSource
import com.ktx.dormitory.student.access.data.remote.AccessRemoteDataSourceImpl
import com.ktx.dormitory.student.access.data.repository.AccessRepositoryImpl
import com.ktx.dormitory.student.access.domain.repository.AccessRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AccessModule {

    @Binds
    @Singleton
    abstract fun bindAccessRemoteDataSource(impl: AccessRemoteDataSourceImpl): AccessRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAccessRepository(impl: AccessRepositoryImpl): AccessRepository

    companion object {
        @Provides
        @Singleton
        fun provideAccessApi(retrofit: Retrofit): AccessApiService = retrofit.create(AccessApiService::class.java)
    }
}
