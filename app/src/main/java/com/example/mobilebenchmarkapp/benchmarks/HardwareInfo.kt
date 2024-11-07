package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.app.ActivityManager
import android.os.Environment
import android.os.StatFs
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt

class HardwareInfo(private val context: Context)
{

    private val benchmarkFile: File = File(context.filesDir, "benchmark_results.txt")

    fun getInfo()
    {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRamMb = memoryInfo.totalMem / (1024 * 1024)
        val totalRamGb = totalRamMb / 1024
        val availableRamMb = memoryInfo.availMem / (1024 * 1024)
        val availableRamGb = availableRamMb / 1024

        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalStorageMb = (statFs.blockCountLong * statFs.blockSizeLong) / (1024 * 1024)
        val totalStorageGb = totalStorageMb / 1024
        val availableStorageMb = (statFs.availableBlocksLong * statFs.blockSizeLong) / (1024 * 1024)
        val availableStorageGb = availableStorageMb / 1024

        val batteryCapacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        val healthStatus = when (batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            else -> "Unknown"
        }


        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val screenDensity = displayMetrics.densityDpi
        val screenSizeInches = sqrt(
            (screenWidth / displayMetrics.xdpi.toDouble()).pow(2.0) +
                    (screenHeight / displayMetrics.ydpi.toDouble()).pow(2.0)
        )

        val info = """
            Complete Hardware Overview:
            - Device Model: ${Build.MODEL} (${Build.MANUFACTURER})
            - Brand: ${Build.BRAND}
            - Product: ${Build.PRODUCT}
            - Hardware: ${Build.HARDWARE}
            - Device: ${Build.DEVICE}
            - Android Version: ${Build.VERSION.RELEASE} (API Level ${Build.VERSION.SDK_INT})
            - CPU Architecture: ${Build.SUPPORTED_ABIS.joinToString(", ")}

            Memory:
            - Total RAM: ${totalRamMb} MB (${totalRamGb} GB)
            - Available RAM: ${availableRamMb} MB (${availableRamGb} GB)

            Storage:
            - Total Internal Storage: ${totalStorageMb} MB (${totalStorageGb} GB)
            - Available Internal Storage: ${availableStorageMb} MB (${availableStorageGb} GB)

            Battery:
            - Capacity: ${batteryCapacity}%
            - Health Status: $healthStatus

            Display:
            - Resolution: ${screenWidth} x ${screenHeight} pixels
            - Density: ${screenDensity} dpi
            - Approximate Screen Size: ${"%.2f".format(screenSizeInches)} inches
        """.trimIndent()


        // Scrierea informațiilor în fișier
        benchmarkFile.writeText("Hardware Information:\n")
        benchmarkFile.appendText(info)
    }
}
