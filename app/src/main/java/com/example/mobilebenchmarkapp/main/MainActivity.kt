package com.example.mobilebenchmarkapp.main

import HardwareInfo
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
fun BenchmarkScreen(modifier: Modifier = Modifier)
{
    val context = LocalContext.current
    var benchmarkResults by remember { mutableStateOf("") }
    var hardwareInfo by remember { mutableStateOf("") }


    val cpuBenchmark = CpuBenchmark { result -> benchmarkResults += "$result\n" }
   // val gpuBenchmark = GpuBenchmark { result -> benchmarkResults += "$result\n" }
    val memoryBenchmark = MemoryBenchmark { result -> benchmarkResults += "$result\n" }
    val hardwareInfoProvider = HardwareInfo(context)

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mobile Benchmark App", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Butoane pentru rulare benchmark-uri
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Primul rând de butoane
                BenchmarkButtonRow(
                    buttonText1 = "Run CPU Benchmark",
                    onClick1 = { cpuBenchmark.run() },
                    buttonText2 = "Run GPU Benchmark",
                    onClick2 = {  }
                )

                // Al doilea rând de butoane
                BenchmarkButtonRow(
                    buttonText1 = "Run Memory Benchmark",
                    onClick1 = { memoryBenchmark.run() },
                    buttonText2 = "Show Hardware Info",
                    onClick2 = {
                        // Obține și afișează informațiile hardware
                        hardwareInfoProvider.getInfo(infoTextView = null) 
                        hardwareInfo = hardwareInfoProvider.getInfoAsString()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Afișare rezultate benchmark
        if (benchmarkResults.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                        Text(text = benchmarkResults, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // Afișare informații hardware
        if (hardwareInfo.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                        Text(text = hardwareInfo, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}



//composable pentru fiecare rand de butoane

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


//functie pentru stergerea rezultatelor anterioare din fisier
private fun clearBenchmarkResults(context: Context)
{
    val file = File(context.filesDir, "benchmark_results.txt")
    file.writeText("")
}

//functie pentru citirea rezultatelor din fisier
private fun readBenchmarkResults(context: Context): String
{
    val file = File(context.filesDir, "benchmark_results.txt")
    return if (file.exists())
    {
        file.readText()

    } else {
        "No results available"
    }
}