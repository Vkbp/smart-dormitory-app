package com.ktx.dormitory.di.feature

import com.ktx.dormitory.student.face.data.remote.FaceApiService
import com.ktx.dormitory.student.face.data.remote.FaceRemoteDataSource
import com.ktx.dormitory.student.face.data.remote.FaceRemoteDataSourceImpl
import com.ktx.dormitory.student.face.data.repository.FaceRepositoryImpl
import com.ktx.dormitory.student.face.domain.repository.FaceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FaceModule {

    @Binds
    @Singleton
    abstract fun bindFaceRemoteDataSource(impl: FaceRemoteDataSourceImpl): FaceRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFaceRepository(impl: FaceRepositoryImpl): FaceRepository

    companion object {
        @Provides
        @Singleton
        fun provideFaceApi(retrofit: Retrofit): FaceApiService = retrofit.create(FaceApiService::class.java)
    }
}
