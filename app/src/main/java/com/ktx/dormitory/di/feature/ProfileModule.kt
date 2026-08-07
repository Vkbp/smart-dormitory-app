package com.ktx.dormitory.di.feature

import com.ktx.dormitory.shared.profile.data.local.ProfileLocalDataSource
import com.ktx.dormitory.shared.profile.data.local.ProfileLocalDataSourceImpl
import com.ktx.dormitory.shared.profile.data.remote.ProfileApiService
import com.ktx.dormitory.shared.profile.data.remote.ProfileRemoteDataSource
import com.ktx.dormitory.shared.profile.data.remote.ProfileRemoteDataSourceImpl
import com.ktx.dormitory.shared.profile.data.repository.ProfileRepositoryImpl
import com.ktx.dormitory.shared.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRemoteDataSource(impl: ProfileRemoteDataSourceImpl): ProfileRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindProfileLocalDataSource(impl: ProfileLocalDataSourceImpl): ProfileLocalDataSource

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    companion object {
        @Provides
        @Singleton
        fun provideProfileApi(retrofit: Retrofit): ProfileApiService = retrofit.create(ProfileApiService::class.java)
    }
}
