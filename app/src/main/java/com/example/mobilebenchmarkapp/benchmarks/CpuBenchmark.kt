package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import java.io.File
import kotlin.system.measureTimeMillis

fun runCpuBenchmark(context: Context) {

    val file = File(context.filesDir, "benchmark_results.txt")
    file.writeText("CPU Benchmark Results:\n")


    // 1. Factorial calculation test
    val factorialTime = measureTimeMillis {
        calculateFactorial(100000)
    }
    file.appendText("Test: Factorial Calculation\nDetails: Calculating the factorial of a large number (100,000)\nTime: $factorialTime ms\n\n")


    // 2. Fibonacci sequence generation test
    val fibonacciTime = measureTimeMillis {
        generateFibonacci(50000)
    }
    file.appendText("Test: Fibonacci Sequence Generation\nDetails: Generating the first 50,000 Fibonacci terms\nTime: $fibonacciTime ms\n\n")


    // 3. Bubble Sort test
    val bubbleSortTime = measureTimeMillis {
        bubbleSort(generateRandomArray(50000))
    }
    file.appendText("Test: Bubble Sort\nDetails: Sorting an array of 50,000 elements\nTime: $bubbleSortTime ms\n\n")


    // 4. Quick Sort test
    val quickSortTime = measureTimeMillis {
        quickSort(generateRandomArray(50000), 0, 49999)
    }
    file.appendText("Test: Quick Sort\nDetails: Sorting an array of 50,000 elements\nTime: $quickSortTime ms\n\n")
}

// Functie pentru calcul factorial
fun calculateFactorial(n: Int): Long {
    var result = 1L

    for (i in 2..n) {
        result *= i
    }
    return result
}

// Functie pentru generarea termenilor Fibonacci
fun generateFibonacci(n: Int): Long {
    var a = 0L
    var b = 1L

    for (i in 2..n) {
        val temp = a + b
        a = b
        b = temp
    }
    return b
}

// Bubble Sort
fun bubbleSort(array: IntArray): IntArray {
    val n = array.size

    for (i in 0 until n - 1) {
        for (j in 0 until n - i - 1) {
            if (array[j] > array[j + 1]) {
                val temp = array[j]
                array[j] = array[j + 1]
                array[j + 1] = temp
            }
        }
    }
    return array
}

// Quick Sort
fun quickSort(array: IntArray, low: Int, high: Int) {
    if (low < high) {
        val pi = partition(array, low, high)
        quickSort(array, low, pi - 1)
        quickSort(array, pi + 1, high)
    }
}

fun partition(array: IntArray, low: Int, high: Int): Int {
    val pivot = array[high]
    var i = (low - 1)

    for (j in low until high) {
        if (array[j] <= pivot) {
            i++
            val temp = array[i]
            array[i] = array[j]
            array[j] = temp
        }
    }
    val temp = array[i + 1]
    array[i + 1] = array[high]
    array[high] = temp
    return i + 1
}

// functie pentru a genera un sir in mod aleator
fun generateRandomArray(size: Int): IntArray {
    return IntArray(size) { (0..1000).random() }
}
