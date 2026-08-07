package com.ktx.dormitory.di.feature

import com.ktx.dormitory.student.room.data.remote.RoomApiService
import com.ktx.dormitory.student.room.data.remote.RoomRemoteDataSource
import com.ktx.dormitory.student.room.data.remote.RoomRemoteDataSourceImpl
import com.ktx.dormitory.student.room.data.repository.RoomRepositoryImpl
import com.ktx.dormitory.student.room.domain.repository.RoomRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomModule {

    @Binds
    @Singleton
    abstract fun bindRoomRemoteDataSource(impl: RoomRemoteDataSourceImpl): RoomRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindRoomRepository(impl: RoomRepositoryImpl): RoomRepository

    companion object {
        @Provides
        @Singleton
        fun provideRoomApi(retrofit: Retrofit): RoomApiService = retrofit.create(RoomApiService::class.java)
    }
}
