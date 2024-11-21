package com.example.mobilebenchmarkapp.main

import GpuBenchmark
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

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
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
    var cpuResults by remember { mutableStateOf("CPU Benchmark: \n") }
    var gpuResults by remember { mutableStateOf("GPU Benchmark: \n") }
    var memoryResults by remember { mutableStateOf("Memory Benchmark: \n") }
    var hardwareInfoResults by remember { mutableStateOf("Hardware Information: \n") }

    val cpuBenchmark = CpuBenchmark(context) { result -> cpuResults += "\n$result" }
    val memoryBenchmark = MemoryBenchmark(context) { result -> memoryResults += "\n$result" }
    val gpuBenchmark = GpuBenchmark(context) { result -> gpuResults += "\n$result" }
    val hardwareInfoProvider = HardwareInfo(context)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mobile Benchmark App", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        BenchmarkButtonRow(
            buttonText1 = "Run CPU Benchmark",
            onClick1 = {
                cpuResults = "CPU Benchmark: \n"
                cpuBenchmark.run()
            },
            buttonText2 = "Run GPU Benchmark",
            onClick2 = {
                gpuResults = "GPU Benchmark: \n"
                gpuBenchmark.run()
            }
        )

        BenchmarkButtonRow(
            buttonText1 = "Run Memory Benchmark",
            onClick1 = {
                memoryResults = "Memory Benchmark: \n"
                memoryBenchmark.run()
            },
            buttonText2 = "Show Hardware Info",
            onClick2 = {
                hardwareInfoResults = "Hardware Information: \n${hardwareInfoProvider.getInfoAsString()}"
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            item { Text(cpuResults, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodyLarge) }
            item { Text(gpuResults, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodyLarge) }
            item { Text(memoryResults, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodyLarge) }
            item { Text(hardwareInfoResults, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodyLarge) }
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


