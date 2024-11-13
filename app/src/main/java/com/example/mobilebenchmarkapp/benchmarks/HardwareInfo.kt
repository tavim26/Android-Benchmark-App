import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.app.ActivityManager
import android.os.Environment
import android.os.StatFs
import android.widget.TextView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.math.pow
import kotlin.math.sqrt

class HardwareInfo(private val context: Context)
{

    private val benchmarkFile: File = File(context.filesDir, "benchmark_results.txt")

    private var totalRamMb: Long = 0
    private var availableRamMb: Long = 0
    private var totalStorageMb: Long = 0
    private var availableStorageMb: Long = 0
    private var batteryCapacity: Int = 0
    private var healthStatus: String = "Unknown"
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var screenDensity: Int = 0
    private var screenSizeInches: Double = 0.0

    init {
        calculateHardwareInfo()
    }

    private fun calculateHardwareInfo()
    {
        // Serviciul de gestionare al activităților
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Serviciul de gestionare al bateriei
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

        // Memorie
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        totalRamMb = memoryInfo.totalMem / (1024 * 1024)
        availableRamMb = memoryInfo.availMem / (1024 * 1024)

        // Stocare internă
        val statFs = StatFs(Environment.getDataDirectory().path)
        totalStorageMb = (statFs.blockCountLong * statFs.blockSizeLong) / (1024 * 1024)
        availableStorageMb = (statFs.availableBlocksLong * statFs.blockSizeLong) / (1024 * 1024)

        // Baterie
        batteryCapacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        healthStatus = when (batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failure"
            else -> "Unknown"
        }

        // Ecran
        val displayMetrics = context.resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
        screenDensity = displayMetrics.densityDpi
        screenSizeInches = sqrt(
            (screenWidth / displayMetrics.xdpi.toDouble()).pow(2.0) +
                    (screenHeight / displayMetrics.ydpi.toDouble()).pow(2.0)
        )
    }

    fun getInfo(infoTextView: TextView?)
    {
        val info = getInfoAsString()

        benchmarkFile.writeText("Hardware Information:\n")
        benchmarkFile.appendText(info)
        infoTextView?.text = info
    }

    fun getInfoAsString(): String
    {
        return """
            Complete Hardware Overview:
            - Device Model: ${Build.MODEL} (${Build.MANUFACTURER})
            - Brand: ${Build.BRAND}
            - Product: ${Build.PRODUCT}
            - Hardware: ${Build.HARDWARE}
            - Device: ${Build.DEVICE}
            - Android Version: ${Build.VERSION.RELEASE} (API Level ${Build.VERSION.SDK_INT})
            - CPU Architecture: ${Build.SUPPORTED_ABIS.joinToString(", ")}
            - Processor Model: ${Build.HARDWARE}
            - Processor Frequency: ${getProcessorFrequency()} MHz
            - Number of Cores: ${getProcessorCores()}

            Memory:
            - Total RAM: ${totalRamMb} MB (${totalRamMb / 1024} GB)
            - Available RAM: ${availableRamMb} MB (${availableRamMb / 1024} GB)

            Storage:
            - Total Internal Storage: ${totalStorageMb} MB (${totalStorageMb / 1024} GB)
            - Available Internal Storage: ${availableStorageMb} MB (${availableStorageMb / 1024} GB)

            Battery:
            - Capacity: ${batteryCapacity}%
            - Health Status: $healthStatus

            Display:
            - Resolution: ${screenWidth} x ${screenHeight} pixels
            - Density: ${screenDensity} dpi
            - Approximate Screen Size: ${"%.2f".format(screenSizeInches)} inches
        """.trimIndent()
    }

    private fun getProcessorFrequency(): Int
    {
        return try {

            val reader = BufferedReader(FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"))
            val freq = reader.readLine()
            reader.close()
            freq.toInt() / 1000

        } catch (e: Exception) {
            0
        }
    }

    private fun getProcessorCores(): Int {
        return Runtime.getRuntime().availableProcessors()
    }
}
