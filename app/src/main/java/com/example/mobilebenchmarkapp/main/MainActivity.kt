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

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)


        setContent {

            //setare tema principala a aplicatiei folosind jetpack compose
            MobileBenchmarkAppTheme {

                //scaffold ofera structura pentru UI
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    //apel functie pentru afisare ecran principal
                    BenchmarkScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}




//composable pentru ecranul principal al aplicatiei
@Composable
fun BenchmarkScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current  //conext curent aplicatie
    var benchmarkResults by remember { mutableStateOf("") } //stocare rezultate de benchmark

    // instantiere obiecte de benchmark
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

        //titlul aplicatiei
        Text("Mobile Benchmark App", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Butoane pentru benchmark, puse in lazycolumn pentru flexibilitate
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            //primul rand butoane (CPU si GPU benchmark)
            item {
                BenchmarkButtonRow(
                    buttonText1 = "Run CPU Benchmark",
                    onClick1 = {
                        clearBenchmarkResults(context)
                        cpuBenchmark.run()
                        benchmarkResults = readBenchmarkResults(context)
                    },
                    buttonText2 = "Run GPU Benchmark",
                    onClick2 = {
                        clearBenchmarkResults(context)
                        gpuBenchmark.run()
                        benchmarkResults = readBenchmarkResults(context)
                    }
                )
            }

            item {

                //al doilea rand de butoane (Memory si HardwareInfo)
                BenchmarkButtonRow(
                    buttonText1 = "Run Memory Benchmark",
                    onClick1 = {
                        clearBenchmarkResults(context)
                        memoryBenchmark.run()
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

        // afisare rezultate benchmark
        if (benchmarkResults.isNotEmpty())
        {
            // Scroll pentru rezultate
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {

                //rezultate puse intr-o caseta

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {

                        //rez puse intr-un card
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
