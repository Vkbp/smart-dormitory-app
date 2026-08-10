package com.ktx.dormitory.di.database

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SupportFactory
import com.ktx.dormitory.core.common.Constants
import com.ktx.dormitory.data.local.AppDatabase
import com.ktx.dormitory.shared.profile.data.local.UserProfileDao
import com.ktx.dormitory.student.payment.data.local.InvoiceDao
import com.ktx.dormitory.student.access.data.local.AccessLogDao
import com.ktx.dormitory.student.access.data.local.CurfewRequestDao
import com.ktx.dormitory.data.common.local.PendingSyncDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val passphrase = Constants.DB_PASSPHRASE.toByteArray()
        val factory = SupportFactory(passphrase)
        
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "smart_dorm_db",
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAccessLogDao(db: AppDatabase): AccessLogDao = db.accessLogDao()

    @Provides
    fun provideCurfewRequestDao(db: AppDatabase): CurfewRequestDao = db.curfewRequestDao()

    @Provides
    fun provideUserProfileDao(db: AppDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    fun provideInvoiceDao(db: AppDatabase): InvoiceDao = db.invoiceDao()

    @Provides
    fun providePendingSyncDao(db: AppDatabase): PendingSyncDao = db.pendingSyncDao()
}
