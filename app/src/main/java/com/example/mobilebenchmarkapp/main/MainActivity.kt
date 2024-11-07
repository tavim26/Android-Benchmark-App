package com.example.mobilebenchmarkapp.main

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment

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

    // Instanțierea obiectelor de benchmark
    val cpuBenchmark = CpuBenchmark(context)
    val gpuBenchmark = GpuBenchmark(context)
    val memoryBenchmark = MemoryBenchmark(context)
    val hardwareInfo = HardwareInfo(context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mobile Benchmark App", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Butoane pentru benchmark
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                BenchmarkButtonRow(
                    buttonText1 = "Run CPU Benchmark",
                    onClick1 = {
                        clearBenchmarkResults(context)
                        cpuBenchmark.run() // Apelăm metoda din clasă
                        benchmarkResults = readBenchmarkResults(context)
                    },
                    buttonText2 = "Run GPU Benchmark",
                    onClick2 = {
                        clearBenchmarkResults(context)
                        gpuBenchmark.run() // Apelăm metoda din clasă
                        benchmarkResults = readBenchmarkResults(context)
                    }
                )
            }

            item {
                BenchmarkButtonRow(
                    buttonText1 = "Run Memory Benchmark",
                    onClick1 = {
                        clearBenchmarkResults(context)
                        memoryBenchmark.run() // Apelăm metoda din clasă
                        benchmarkResults = readBenchmarkResults(context)
                    },
                    buttonText2 = "Show Hardware Info",
                    onClick2 = {
                        clearBenchmarkResults(context)
                        hardwareInfo.getInfo()
                        benchmarkResults = readBenchmarkResults(context)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Afișare rezultat benchmark
        if (benchmarkResults.isNotEmpty()) {
            // Scroll pe rezultatele afișate
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Text(
                                text = "Results:\n$benchmarkResults",
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun BenchmarkButtonRow(
    buttonText1: String,
    onClick1: () -> Unit,
    buttonText2: String,
    onClick2: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onClick1,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(buttonText1)
        }

        Button(
            onClick = onClick2,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(buttonText2)
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
