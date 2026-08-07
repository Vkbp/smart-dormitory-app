package com.ktx.dormitory.di.feature

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
abstract class ExtensionModule {

    @Binds
    @Singleton
    abstract fun bindExtensionRepository(impl: ExtensionRepositoryImpl): ExtensionRepository

    companion object {
        @Provides
        @Singleton
        fun provideExtensionApi(retrofit: Retrofit): ExtensionApiService = retrofit.create(ExtensionApiService::class.java)
    }
}
