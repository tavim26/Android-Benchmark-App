package com.example.mobilebenchmarkapp.benchmarks

import java.io.File
import android.content.Context
import kotlin.system.measureTimeMillis

// Funcție pentru a converti bytes în gigabytes
fun bytesToGigabytes(bytes: Long): Double {
    return bytes.toDouble() / 1_073_741_824
}

// Testează performanța RAM
fun runMemoryBenchmark(context: Context) {
    val runtime = Runtime.getRuntime()

    // Test de alocare masivă a memoriei RAM
    val allocationTestResult = measureTimeMillis {
        val largeArray = IntArray(10_000_000) // Alocă 10 milioane de int-uri
        for (i in largeArray.indices) {
            largeArray[i] = i * 2
        }
    }

    // Memorie liberă și totală
    val freeMemory = runtime.freeMemory()
    val totalMemory = runtime.totalMemory()
    val usedMemory = totalMemory - freeMemory

    // Convertim valorile în GB
    val freeMemoryGB = bytesToGigabytes(freeMemory)
    val totalMemoryGB = bytesToGigabytes(totalMemory)
    val usedMemoryGB = bytesToGigabytes(usedMemory)

    val result = """
        RAM Memory Benchmark:
        - Allocating 10 million integers took: $allocationTestResult ms
        - Used Memory: $usedMemory bytes (${"%.2f".format(usedMemoryGB)} GB)
        - Free Memory: $freeMemory bytes (${String.format("%.2f", freeMemoryGB)} GB)
        - Total Memory: $totalMemory bytes (${String.format("%.2f", totalMemoryGB)} GB)
    """.trimIndent()

    // Scrie rezultatul în fișier
    val file = File(context.filesDir, "benchmark_results.txt")
    file.appendText("$result\n")
}
