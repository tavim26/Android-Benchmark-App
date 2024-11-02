package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.app.ActivityManager
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import java.io.File

fun showHardwareInfo(context: Context) {
    
    // manageri necesari (de activitate si de baterie)
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    // informatii despre memoria RAM
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    val totalRamMb = memoryInfo.totalMem / (1024 * 1024)
    val totalRamGb = totalRamMb / 1024  
    val availableRamMb = memoryInfo.availMem / (1024 * 1024)  
    val availableRamGb = availableRamMb / 1024 

    // Informatii despre capacitatea de stocare
    val statFs = StatFs(Environment.getDataDirectory().path)
    val totalStorageMb = (statFs.blockCountLong * statFs.blockSizeLong) / (1024 * 1024)  
    val totalStorageGb = totalStorageMb / 1024  
    val availableStorageMb = (statFs.availableBlocksLong * statFs.blockSizeLong) / (1024 * 1024)  
    val availableStorageGb = availableStorageMb / 1024 

    // informatii despre capacitatea bateriei
    val batteryCapacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    // starea de sanatate a bateriei
    val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val healthStatus = when (batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
        else -> "Unknown"
    }

    // informatii despre ecran
    val displayMetrics = context.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels
    val screenDensity = displayMetrics.densityDpi
    val screenSizeInches = Math.sqrt(
        Math.pow(screenWidth / displayMetrics.xdpi.toDouble(), 2.0) +
                Math.pow(screenHeight / displayMetrics.ydpi.toDouble(), 2.0)
    )

    // informatii despre configurarea hardware a dispozitivului
    val result = """
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

    // scriere rezultat in fisier
    val file = File(context.filesDir, "benchmark_results.txt")
    file.appendText("$result\n")
}
