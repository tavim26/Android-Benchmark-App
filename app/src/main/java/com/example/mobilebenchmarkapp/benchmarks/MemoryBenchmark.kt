package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import java.io.File
import kotlin.system.measureTimeMillis

class MemoryBenchmark(private val context: Context) {

    private val benchmarkFile: File = File(context.filesDir, "benchmark_results.txt")

    fun run() {
        benchmarkFile.writeText("Memory Benchmark Results:\n")

        // Rularea fiecărui test de memorie
        runMatrixAllocationTest()
        runMemoryReadWriteTest()
    }

    private fun runMatrixAllocationTest() {
        val time = measureTimeMillis {
            allocateMatrices(10, 1000)
        }
        benchmarkFile.appendText(
            "Test: Matrix Allocation\n" +
                    "Details: Allocating 10 matrices of size 1000x1000\n" +
                    "Average Time: $time ms\n\n"
        )
    }

    private fun runMemoryReadWriteTest() {
        val time = measureTimeMillis {
            performReadWriteTest(1000)
        }
        benchmarkFile.appendText(
            "Test: Memory Read/Write\n" +
                    "Details: Performing read and write operations on a 1000x1000 matrix\n" +
                    "Time: $time ms\n\n"
        )
    }

    private fun allocateMatrices(matrixCount: Int, size: Int) {
        val matrices = Array(matrixCount) { Array(size) { IntArray(size) } }
        for (matrix in matrices) {
            for (row in matrix) {
                for (i in row.indices) {
                    row[i] = i // Populare pentru a asigura folosirea memoriei
                }
            }
        }
    }

    private fun performReadWriteTest(size: Int) {
        val matrix = Array(size) { IntArray(size) { it } }

        // Operație de scriere
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                matrix[i][j] += 1
            }
        }

        // Operație de citire
        var sum = 0
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                sum += matrix[i][j]
            }
        }
    }
}
