package com.example.mobilebenchmarkapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mobilebenchmarkapp.ui.theme.MobileBenchmarkAppTheme
import com.example.mobilebenchmarkapp.benchmarks.*
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileBenchmarkAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BenchmarkScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BenchmarkScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var benchmarkResults by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text("Mobile Benchmark App", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Button for CPU Benchmark
        Button(onClick = {
            clearBenchmarkResults(context)
            runCpuBenchmark(context) // Apelează funcția de benchmark
            benchmarkResults = readBenchmarkResults(context) // Citește rezultatul
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Run CPU Benchmark")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button for Memory Benchmark
        Button(onClick = {
            clearBenchmarkResults(context)
            runMemoryBenchmark(context)
            benchmarkResults = readBenchmarkResults(context)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Run Memory Benchmark")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button for GPU Benchmark
        Button(onClick = {
            clearBenchmarkResults(context)
            runGpuBenchmark(context)
            benchmarkResults = readBenchmarkResults(context)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Run GPU Benchmark")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Button for Hardware Information
        Button(onClick = {
            clearBenchmarkResults(context)
            showHardwareInfo(context)
            benchmarkResults = readBenchmarkResults(context)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Show Hardware Information")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display benchmark result
        if (benchmarkResults.isNotEmpty()) {
            Text("Result:\n$benchmarkResults")
        }
    }
}

private fun clearBenchmarkResults(context: Context) {
    val file = File(context.filesDir, "benchmark_results.txt")
    file.writeText("")
}

private fun readBenchmarkResults(context: Context): String {
    val file = File(context.filesDir, "benchmark_results.txt")
    return if (file.exists()) {
        file.readText()
    } else {
        "No results available"
    }
}
