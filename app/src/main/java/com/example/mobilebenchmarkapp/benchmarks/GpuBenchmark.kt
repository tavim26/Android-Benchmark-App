package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import java.io.File

class GpuBenchmark(private val context: Context)
{

    fun run()
    {
        val file = File(context.filesDir, "benchmark_results.txt")
        file.writeText("GPU Benchmark Results:\n")

        //TODO
    }
}
