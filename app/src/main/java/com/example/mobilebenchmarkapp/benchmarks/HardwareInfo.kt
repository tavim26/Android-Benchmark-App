package com.example.mobilebenchmarkapp.benchmarks

import java.io.File
import android.content.Context
import android.os.Build
import android.app.ActivityManager
import android.util.DisplayMetrics

fun showHardwareInfo(context: Context) {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val displayMetrics = context.resources.displayMetrics

    // Afișează informațiile hardware detaliate
    val result = """
        Hardware Info:
        - Device Model: ${Build.MODEL}
        - Manufacturer: ${Build.MANUFACTURER}
        - Brand: ${Build.BRAND}
        - Product: ${Build.PRODUCT}
        - Hardware: ${Build.HARDWARE}
        - Device: ${Build.DEVICE}
        - Android Version: ${Build.VERSION.RELEASE}
        - API Level: ${Build.VERSION.SDK_INT}
        - CPU Architecture: ${Build.SUPPORTED_ABIS.joinToString(", ")}
        - RAM Total: ${memoryInfo.totalMem / (1024 * 1024)} MB
        - Screen Resolution: ${displayMetrics.widthPixels} x ${displayMetrics.heightPixels} pixels
        - Screen Density: ${displayMetrics.densityDpi} dpi
    """.trimIndent()

    // Scrie rezultatul în fișier
    val file = File(context.filesDir, "benchmark_results.txt")
    file.appendText("$result\n")
}


