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

        //serviciul de gestionare al activitatilor
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        //serviciul de gestionare al bateriei
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager


        //obtinere info despre memoria totala si disponibila in momentul masurarii
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRamMb = memoryInfo.totalMem / (1024 * 1024)
        val totalRamGb = totalRamMb / 1024
        val availableRamMb = memoryInfo.availMem / (1024 * 1024)
        val availableRamGb = availableRamMb / 1024

        //informatii despre stocare interna
        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalStorageMb = (statFs.blockCountLong * statFs.blockSizeLong) / (1024 * 1024)
        val totalStorageGb = totalStorageMb / 1024
        val availableStorageMb = (statFs.availableBlocksLong * statFs.blockSizeLong) / (1024 * 1024)
        val availableStorageGb = availableStorageMb / 1024


        //informatii despre baterie (stare de sanatate)
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



        //obtinere informatii despre ecranul dispozitivului
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val screenDensity = displayMetrics.densityDpi

        //calcul dimensiune aproximativa a ecranului in inch
        val screenSizeInches = sqrt(
            (screenWidth / displayMetrics.xdpi.toDouble()).pow(2.0) +
                    (screenHeight / displayMetrics.ydpi.toDouble()).pow(2.0)
        )



        //formatare informatii pentru afisare
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


        // scrierea informatiilor in fisier
        benchmarkFile.writeText("Hardware Information:\n")
        benchmarkFile.appendText(info)
    }
}
