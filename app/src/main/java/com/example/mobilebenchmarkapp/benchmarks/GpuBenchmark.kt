package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import android.opengl.GLES20
import java.io.File
import kotlin.system.measureTimeMillis

class GpuBenchmark(private val context: Context) {

    fun run() {
        val file = File(context.filesDir, "benchmark_results.txt")
        val renderingTime = measureTimeMillis { run3DRenderingTest() }
        file.appendText("GPU Benchmark - 3D Rendering Time: $renderingTime ms\n")
    }

    private fun run3DRenderingTest() {
        initOpenGL()
        val program = createShaderProgram()

        // Simplificăm exemplele de obiecte (cube și piramidă), înlăturând funcții separate
        val simpleCube = floatArrayOf(
            -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f
        )
        renderObject(program, simpleCube)
    }

    private fun initOpenGL() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClearColor(0.1f, 0.1f, 0.3f, 1.0f)
    }

    private fun createShaderProgram(): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, loadShaderSource("shader.vert"))
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, loadShaderSource("shader.frag"))
        return GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
            GLES20.glUseProgram(this)
        }
    }

    private fun loadShaderSource(filename: String): String {
        return context.assets.open("com.example.mobilebenchmarkapp.shaders/$filename").bufferedReader().use { it.readText() }
    }

    private fun renderObject(program: Int, vertices: FloatArray) {
        val vertexBuffer = java.nio.ByteBuffer.allocateDirect(vertices.size * 4)
            .order(java.nio.ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(vertices).position(0) }

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.size / 3)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also {
            GLES20.glShaderSource(it, shaderCode)
            GLES20.glCompileShader(it)
        }
    }
}
