package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import java.io.File
import kotlin.math.pow
import kotlin.system.measureTimeMillis

fun runCpuBenchmark(context: Context) {
    val file = File(context.filesDir, "benchmark_results.txt")
    file.writeText("CPU Benchmark Results:\n") // Șterge conținutul anterior

    // 1. Test de calcul factorial
    val factorialTime = measureTimeMillis {
        calculateFactorial(1000000)
    }
    file.appendText("Test: Calcul factorial\nDetalii: Calculul factorialului unui număr mare (1.000.000)\nTimp: $factorialTime ms\n\n")


    // 3. Test de multiplicări de matrice
    val matrixMultiplicationTime = measureTimeMillis {
        multiplyMatrices(500)
    }
    file.appendText("Test: Multiplicare de matrice\nDetalii: Multiplicarea a două matrice 500x500\nTimp: $matrixMultiplicationTime ms\n\n")
}

// Funcție pentru calcul factorial
fun calculateFactorial(n: Int): Long {
    var result = 1L
    for (i in 2..n) {
        result *= i
    }
    return result
}


// Funcție pentru multiplicare de matrice
fun multiplyMatrices(size: Int) {
    val matrixA = Array(size) { IntArray(size) { (0..100).random() } }
    val matrixB = Array(size) { IntArray(size) { (0..100).random() } }
    val result = Array(size) { IntArray(size) }

    for (i in 0 until size) {
        for (j in 0 until size) {
            for (k in 0 until size) {
                result[i][j] += matrixA[i][k] * matrixB[k][j]
            }
        }
    }
}
