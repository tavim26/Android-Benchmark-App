package com.example.mobilebenchmarkapp.benchmarks

import java.io.File
import android.content.Context
import kotlin.system.measureTimeMillis



fun runMemoryBenchmark(context: Context) {
    val file = File(context.filesDir, "benchmark_results.txt")
    file.writeText("Memory Benchmark Results:\n")
}
