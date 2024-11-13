import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executors

class GpuBenchmark(private val context: Context, private val onResult: (String) -> Unit) : GLSurfaceView.Renderer {

    private val executorService = Executors.newSingleThreadExecutor()
    private val uiHandler = Handler(Looper.getMainLooper()) // Handler pentru a actualiza UI-ul

    private var squareProgram: Int = 0
    private var cubeProgram: Int = 0

    private val squareVertices = floatArrayOf(
        -0.5f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
        0.5f, 0.5f, 0.0f
    )

    private val cubeVertices = floatArrayOf(
        -0.5f, -0.5f, -0.5f,
        0.5f, -0.5f, -0.5f,
        -0.5f, 0.5f, -0.5f,
        0.5f, 0.5f, -0.5f,
        0.5f, -0.5f, 0.5f,
        -0.5f, -0.5f, 0.5f,
        0.5f, 0.5f, 0.5f,
        -0.5f, 0.5f, 0.5f
    )

    init {
        // Initializare GLSurfaceView și setări
    }

    fun run() {
        val glSurfaceView = GLSurfaceView(context)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(this)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onSurfaceCreated(gl: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // Creează shaderi pentru obiectele 2D și 3D
        squareProgram = createProgram(SHADER_VERTEX, SHADER_FRAGMENT)
        cubeProgram = createProgram(CUBE_VERTEX_SHADER, CUBE_FRAGMENT_SHADER)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Măsurarea timpului pentru obiectul 2D
        val startTime2D = System.nanoTime()
        drawSquare()
        val endTime2D = System.nanoTime()
        val renderTime2D = (endTime2D - startTime2D) / 1_000_000.0  // Timpul în milisecunde
        Log.d("GPU Benchmark", "2D Render Time: ${"%.2f".format(renderTime2D)} ms")

        // Măsurarea timpului pentru obiectul 3D
        val startTime3D = System.nanoTime()
        drawCube()
        val endTime3D = System.nanoTime()
        val renderTime3D = (endTime3D - startTime3D) / 1_000_000.0  // Timpul în milisecunde
        Log.d("GPU Benchmark", "3D Render Time: ${"%.2f".format(renderTime3D)} ms")

        // Transmiterea rezultatelor către UI
        uiHandler.post {
            onResult("2D Object Render Time: ${"%.2f".format(renderTime2D)} ms\n" +
                    "3D Object Render Time: ${"%.2f".format(renderTime3D)} ms")
        }
    }

    private fun drawSquare() {
        GLES20.glUseProgram(squareProgram)
        val squareBuffer = ByteBuffer.allocateDirect(squareVertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        squareBuffer.put(squareVertices)
        squareBuffer.position(0)

        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, squareBuffer)
        GLES20.glEnableVertexAttribArray(0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)
    }

    private fun drawCube() {
        GLES20.glUseProgram(cubeProgram)
        val cubeBuffer = ByteBuffer.allocateDirect(cubeVertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        cubeBuffer.put(cubeVertices)
        cubeBuffer.position(0)

        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, cubeBuffer)
        GLES20.glEnableVertexAttribArray(0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 8)
    }

    private fun createProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
        val vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    companion object {
        // Shaderi pentru pătratul 2D
        private const val SHADER_VERTEX = """
            attribute vec4 a_Position;
            void main() {
                gl_Position = a_Position;
            }
        """
        private const val SHADER_FRAGMENT = """
            precision mediump float;
            void main() {
                gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
            }
        """
        // Shaderi pentru cubul 3D
        private const val CUBE_VERTEX_SHADER = """
            attribute vec4 a_Position;
            void main() {
                gl_Position = a_Position;
            }
        """
        private const val CUBE_FRAGMENT_SHADER = """
            precision mediump float;
            void main() {
                gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);
            }
        """
    }
}
