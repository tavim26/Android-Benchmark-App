package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import java.io.File
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class MemoryBenchmark(private val context: Context) {

    private val benchmarkFile = File(context.filesDir, "benchmark_results.txt")

    fun run() {
        benchmarkFile.writeText("Memory Benchmark Results:\n")

        // Test de scriere-citire cu dimensiune mai mare
        runWriteReadTest(20) // 100 MB

        // Test de alocare matrice mare în memorie
        runMatrixAllocationTest(10, 1000)
    }

    // Test de scriere-citire optimizat fără coroutine
    private fun runWriteReadTest(mb: Int) {
        val data = ByteArray(mb * 1024 * 1024) { Random.nextBytes(1)[0] }
        val file = File(context.cacheDir, "test_data.bin")

        // Scriere pe blocuri mai mari pentru eficiență
        val writeTime = measureTimeMillis {
            file.outputStream().use { it.write(data) }
        }

        // Citire din fișier într-un bloc mare
        val readTime = measureTimeMillis {
            file.inputStream().use { it.readBytes() }
        }

        // Salvare rezultate în fișierul de benchmark
        benchmarkFile.appendText("Memory Write Test ($mb MB): $writeTime ms\n")
        benchmarkFile.appendText("Memory Read Test ($mb MB): $readTime ms\n\n")

        // Ștergere fișier temporar
        file.delete()
    }

    // Test de alocare matrice mare
    private fun runMatrixAllocationTest(n: Int, m: Int) {
        val allocationTime = measureTimeMillis {
            repeat(n) {
                Array(m) { IntArray(m) { Random.nextInt(0, 100) } }
            }
        }

        benchmarkFile.appendText("Matrix Allocation Test ($n matrices of $m x $m): $allocationTime ms\n\n")
    }
}
