import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GpuBenchmark(private val context: Context) : GLSurfaceView.Renderer {

    private val benchmarkFile = File(context.filesDir, "benchmark_results.txt")
    private var previousFrameTime: Long = 0
    private var accumulatedTime: Double = 0.0
    private var frameCount: Int = 0
    private lateinit var vertexBuffer: FloatBuffer
    private var programHandle: Int = 0

    init {
        if (!benchmarkFile.exists()) {
            try {
                benchmarkFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun run() {
        val glSurfaceView = GLSurfaceView(context)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(this)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onSurfaceCreated(gl: javax.microedition.khronos.opengles.GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        previousFrameTime = System.nanoTime()

        // Inițializarea triunghiului
        val vertices = floatArrayOf(
            0.0f, 0.5f, 0.0f,  // Vârf superior
            -0.5f, -0.5f, 0.0f, // Vârf stânga jos
            0.5f, -0.5f, 0.0f  // Vârf dreapta jos
        )

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)

        val vertexShaderCode = """
            attribute vec4 vPosition;
            void main() {
                gl_Position = vPosition;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
            uniform vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """.trimIndent()

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        programHandle = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    override fun onSurfaceChanged(gl: javax.microedition.khronos.opengles.GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(programHandle)

        val positionHandle = GLES20.glGetAttribLocation(programHandle, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

        val colorHandle = GLES20.glGetUniformLocation(programHandle, "vColor")
        GLES20.glUniform4f(colorHandle, 0.6f, 0.4f, 0.7f, 1.0f)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        GLES20.glDisableVertexAttribArray(positionHandle)

        // Măsurarea timpului de randare pe cadru
        val currentTime = System.nanoTime()
        val frameTimeMs = (currentTime - previousFrameTime) / 1_000_000.0 // Conversie în milisecunde
        previousFrameTime = currentTime

        // Acumularea timpului și numărului de cadre
        accumulatedTime += frameTimeMs
        frameCount++

        // Scrierea în fișier la fiecare 60 de cadre
        if (frameCount >= 60) {
            val averageFrameTime = accumulatedTime / frameCount
            writeBenchmarkResults(averageFrameTime)
            accumulatedTime = 0.0
            frameCount = 0
        }
    }

    private fun writeBenchmarkResults(averageFrameTime: Double) {
        try {
            benchmarkFile.appendText("Average Frame Time (last 60 frames): $averageFrameTime ms\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}