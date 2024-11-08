package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import java.io.File
import kotlin.system.measureTimeMillis

class CpuBenchmark(private val context: Context) {

    private val benchmarkFile: File = File(context.filesDir, "benchmark_results.txt")

    fun run() {
        benchmarkFile.writeText("CPU Benchmark Results:\n")
        runFactorialTest()
        runFibonacciTest()
        runBubbleSortTest()
        runQuickSortTest()
    }

    private fun runFactorialTest()
    {
        val time = measureTimeMillis {
            calculateFactorial(100000)
        }
        benchmarkFile.appendText("Test: Factorial Calculation\nDetails: Calculating the factorial of a large number (100,000)\nTime: $time ms\n\n")
    }

    private fun runFibonacciTest() {
        val time = measureTimeMillis {
            generateFibonacci(50000)
        }
        benchmarkFile.appendText("Test: Fibonacci Sequence Generation\nDetails: Generating the first 50,000 Fibonacci terms\nTime: $time ms\n\n")
    }

    private fun runBubbleSortTest() {
        val time = measureTimeMillis {
            bubbleSort(generateRandomArray(50000))
        }
        benchmarkFile.appendText("Test: Bubble Sort\nDetails: Sorting an array of 50,000 elements\nTime: $time ms\n\n")
    }

    private fun runQuickSortTest() {
        val time = measureTimeMillis {
            quickSort(generateRandomArray(50000), 0, 49999)
        }
        benchmarkFile.appendText("Test: Quick Sort\nDetails: Sorting an array of 50,000 elements\nTime: $time ms\n\n")
    }

    private fun calculateFactorial(n: Int): Long
    {
        var result = 1L

        for (i in 2..n)
        {
            result *= i
        }
        return result
    }

    private fun generateFibonacci(n: Int): Long
    {
        var a = 0L
        var b = 1L
        for (i in 2..n)
        {
            val temp = a + b
            a = b
            b = temp
        }
        return b
    }

    private fun bubbleSort(array: IntArray): IntArray
    {
        val n = array.size
        for (i in 0 until n - 1)
        {
            for (j in 0 until n - i - 1)
            {
                if (array[j] > array[j + 1])
                {
                    val temp = array[j]
                    array[j] = array[j + 1]
                    array[j + 1] = temp
                }
            }
        }
        return array
    }

    private fun quickSort(array: IntArray, low: Int, high: Int)
    {
        if (low < high)
        {
            val pi = partition(array, low, high)
            quickSort(array, low, pi - 1)
            quickSort(array, pi + 1, high)
        }
    }

    private fun partition(array: IntArray, low: Int, high: Int): Int
    {
        val pivot = array[high]
        var i = (low - 1)

        for (j in low until high)
        {
            if (array[j] <= pivot)
            {
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

    private fun generateRandomArray(size: Int): IntArray
    {
        return IntArray(size) { (0..1000).random() }
    }
}
