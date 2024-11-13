package com.example.mobilebenchmarkapp.benchmarks

import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class MemoryBenchmark(private val onResult: (String) -> Unit) {
    private val executor = Executors.newFixedThreadPool(3)

    fun run()
    {
        executor.submit { runWriteTest(10) }
        executor.submit { runReadTest(10) }
        executor.submit { runMatrixAllocationTest(10, 1000) }
    }

    private fun runWriteTest(mb: Int)
    {
        val tempFile = createTempFile()

        val writeTime = measureTimeMillis {

            val data = ByteArray(mb * 1024 * 1024) { Random.nextBytes(1)[0] }
            writeToFile(tempFile, data)

        }

        onResult("Memory Write Test ($mb MB): $writeTime ms")

        tempFile.delete()
    }

    private fun runReadTest(mb: Int) {
        val tempFile = createTempFile()

        val data = ByteArray(mb * 1024 * 1024) { Random.nextBytes(1)[0] }
        writeToFile(tempFile, data)

        val readTime = measureTimeMillis {
            val readData = readFromFile(tempFile)
        }

        onResult("Memory Read Test ($mb MB): $readTime ms")

        tempFile.delete()
    }

    private fun runMatrixAllocationTest(n: Int, m: Int)
    {

        val allocationTime = measureTimeMillis {

            repeat(n) {
                Array(m) { IntArray(m) { Random.nextInt(0, 100) } }
            }
        }

        onResult("Matrix Allocation Test ($n matrices of $m x $m): $allocationTime ms")
    }

    private fun createTempFile(): File {
        return File.createTempFile("benchmark_", ".tmp").apply {
            deleteOnExit()
        }
    }

    private fun writeToFile(file: File, data: ByteArray) {
        RandomAccessFile(file, "rw").use { raf ->
            raf.write(data)
        }
    }

    private fun readFromFile(file: File): ByteArray
    {
        return RandomAccessFile(file, "r").use { raf ->
            val fileLength = raf.length().toInt()
            val buffer = ByteArray(fileLength)
            raf.readFully(buffer)
            buffer
        }
    }
}
