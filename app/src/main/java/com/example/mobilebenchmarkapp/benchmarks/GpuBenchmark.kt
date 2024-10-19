package com.example.mobilebenchmarkapp.benchmarks

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.io.File
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.system.measureTimeMillis
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

fun runGpuBenchmark(context: Context) {
    val gpuBenchmarkResult = measureTimeMillis {
        // Creăm un SurfaceView OpenGL ES pentru a simula un test de GPU
        val glSurfaceView = GLSurfaceView(context)
        glSurfaceView.setEGLContextClientVersion(2) // OpenGL ES 2.0
        glSurfaceView.setRenderer(ComplexRenderer()) // Renderer complex
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        // Lansăm un ciclu de randare pentru o scenă mult mai mare și complexă
        for (i in 1..500) {  // Randăm scena de 500 de ori
            glSurfaceView.requestRender()
        }
    }

    val result = """
        GPU Benchmark:
        - Time to render a large complex 3D scene: $gpuBenchmarkResult ms
    """.trimIndent()

    // Scrie rezultatul în fișier
    val file = File(context.filesDir, "benchmark_results.txt")
    file.appendText("$result\n")
}

// Renderer care randează mai multe cuburi 3D și le rotește pentru a măsura performanța GPU-ului
class ComplexRenderer : GLSurfaceView.Renderer {

    private val cubes = List(100) { Cube() }  // Creăm 100 de cuburi
    private var angle = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Setează culoarea fundalului
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        // Activează testarea adâncimii (depth testing)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        // Initializează fiecare cub
        cubes.forEach { it.initialize() }
    }

    override fun onDrawFrame(gl: GL10?) {
        // Curăță ecranul și buffer-ul de adâncime
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Rotește și randează fiecare cub într-o poziție diferită
        cubes.forEachIndexed { index, cube ->
            cube.draw(angle + index * 0.5f)
        }
        angle += 1.0f
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }
}

// Definirea unui cub 3D simplu
class Cube {

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
    """.trimIndent()

    private val vertices = floatArrayOf(
        -1.0f, 1.0f, -1.0f,  // 0
        -1.0f, -1.0f, -1.0f, // 1
        1.0f, -1.0f, -1.0f,  // 2
        1.0f, 1.0f, -1.0f,   // 3
        -1.0f, 1.0f, 1.0f,   // 4
        -1.0f, -1.0f, 1.0f,  // 5
        1.0f, -1.0f, 1.0f,   // 6
        1.0f, 1.0f, 1.0f     // 7
    )

    private val indices = shortArrayOf(
        0, 1, 2, 0, 2, 3,  // Fața din spate
        4, 5, 6, 4, 6, 7,  // Fața din față
        0, 1, 5, 0, 5, 4,  // Fața din stânga
        3, 2, 6, 3, 6, 7,  // Fața din dreapta
        0, 3, 7, 0, 7, 4,  // Fața de sus
        1, 2, 6, 1, 6, 5   // Fața de jos
    )

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private var program: Int = 0

    fun initialize() {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indices)
        indexBuffer.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    fun draw(angle: Float) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        GLES20.glUniform4fv(colorHandle, 1, floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f), 0)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        val mvpMatrix = getMVPMatrix(angle)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private fun getMVPMatrix(angle: Float): FloatArray {
        // Returnează o matrice simplă de transformare (rotație, perspectivă, etc.)
        return FloatArray(16).apply {
            android.opengl.Matrix.setRotateM(this, 0, angle, 0.5f, 1.0f, 0.0f)
        }
    }
}
