package com.ktx.dormitory.di.common

import com.ktx.dormitory.core.dispatcher.AppCoroutineDispatchers
import com.ktx.dormitory.core.dispatcher.CoroutineDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @Singleton
    fun provideCoroutineDispatchers(): CoroutineDispatchers {
        return AppCoroutineDispatchers()
    }
}
