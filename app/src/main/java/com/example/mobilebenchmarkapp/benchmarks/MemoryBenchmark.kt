package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import java.io.File

class MemoryBenchmark(private val context: Context) {

    fun run()
    {
        val file = File(context.filesDir, "benchmark_results.txt")
        file.writeText("Memory Benchmark Results:\n")
        // TODO
    }
}
