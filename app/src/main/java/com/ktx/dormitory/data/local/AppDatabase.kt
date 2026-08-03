package com.ktx.dormitory.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ktx.dormitory.shared.profile.data.local.UserProfileDao
import com.ktx.dormitory.shared.profile.data.local.UserProfileEntity
import com.ktx.dormitory.student.payment.data.local.InvoiceDao
import com.ktx.dormitory.student.payment.data.local.InvoiceEntity
import com.ktx.dormitory.student.access.data.local.AccessLogDao
import com.ktx.dormitory.student.access.data.local.AccessLogEntity
import com.ktx.dormitory.student.access.data.local.CurfewRequestDao
import com.ktx.dormitory.student.access.data.local.CurfewRequestEntity
import com.ktx.dormitory.data.common.local.PendingSyncDao
import com.ktx.dormitory.data.common.local.PendingSyncEntity

@Database(
    entities = [
        UserProfileEntity::class,
        InvoiceEntity::class,
        AccessLogEntity::class,
        CurfewRequestEntity::class,
        PendingSyncEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun accessLogDao(): AccessLogDao
    abstract fun curfewRequestDao(): CurfewRequestDao
    abstract fun pendingSyncDao(): PendingSyncDao
}
