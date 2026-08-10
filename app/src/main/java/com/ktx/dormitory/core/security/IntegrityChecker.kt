package com.ktx.dormitory.core.security

import android.content.Context
import com.scottyab.rootbeer.RootBeer
import timber.log.Timber

/**
 * Utility for device integrity checks (SEC-07).
 * Rationale: Hardens the app against unauthorized access on rooted devices/emulators.
 */
object IntegrityChecker {

    /**
     * Checks if the device is rooted or running on an emulator.
     */
    fun isDeviceCompromised(context: Context): Boolean {
        val rootBeer = RootBeer(context)
        val isRooted = rootBeer.isRooted
        val isEmulator = isEmulator()

        if (isRooted) Timber.e("Security Alert: Device is ROOTED")
        if (isEmulator) Timber.w("Security Alert: Running on EMULATOR")

        return isRooted
    }

    private fun isEmulator(): Boolean {
        val buildDetails = android.os.Build.FINGERPRINT
        return buildDetails.startsWith("generic")
                || buildDetails.contains("vbox")
                || buildDetails.contains("test-keys")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
    }
}
