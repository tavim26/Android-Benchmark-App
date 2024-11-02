package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import java.io.File

fun runGpuBenchmark(context: Context) {
    val file = File(context.filesDir, "benchmark_results.txt")
    file.writeText("GPU Benchmark Results:\n")
}
