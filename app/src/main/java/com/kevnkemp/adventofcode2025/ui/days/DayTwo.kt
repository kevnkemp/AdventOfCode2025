package com.kevnkemp.adventofcode2025.ui.days

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.text.split

class DayTwo() : Day {
    
    @Composable
    override fun Compose(modifier: Modifier) {
        val context = LocalContext.current

        var part1Duration by remember { mutableLongStateOf(0L) }
        var part2Duration by remember { mutableLongStateOf(0L) }

        var part1Answer: Long? by remember { mutableStateOf(null) }
        var part2Answer: Long? by remember { mutableStateOf(null) }

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val input = buildInput(context, "aoc_25_day2.txt")
                launch {
                    val startTime = System.currentTimeMillis()
                    part1Answer = sumInvalidIdsPart1(input)
                    val endTime = System.currentTimeMillis()
                    part1Duration = (endTime - startTime)

                }
                launch {
                    val startTime = System.currentTimeMillis()
                    part2Answer = sumInvalidIdsPart2(input)
                    val endTime = System.currentTimeMillis()
                    part2Duration = (endTime - startTime)
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            part1Answer?.let {
                Text(text = "Part 1 answer is $part1Answer and took $part1Duration milliseconds")
            } ?: run {
                Text(text = "Calculating Part 1...")
            }
            part2Answer?.let {
                Text(text = "Part 2 answer is $it and took $part2Duration milliseconds")
            } ?: run {
                Text(text = "Calculating Part 2...")
            }
        }
    }

    // Part 1 Solution
    private suspend fun sumInvalidIdsPart1(input: List<String>): Long =
        withContext(Dispatchers.IO) {
            var count = 0L
            for (range in input) {
                val rangeMin = range.split("-")[0].toLong()
                val rangeMax = range.split("-")[1].toLong()

                for (i in rangeMin..rangeMax) {
                    val iString = i.toString()
                    if (iString.length % 2 == 0) {
                        val halfLength = iString.length / 2
                        val firstHalf = iString.substring(0, halfLength)
                        val secondHalf = iString.substring(halfLength)

                        if (firstHalf == secondHalf) {
                            count += i
                        }
                    }
                }
            }
            count
        }

    // Part 2 Solution
    private suspend fun sumInvalidIdsPart2(input: List<String>): Long =
        withContext(Dispatchers.IO) {
            var count = 0L
            for (range in input) {
                val rangeMin = range.split("-")[0].toLong()
                val rangeMax = range.split("-")[1].toLong()
                for (id in rangeMin..rangeMax) {
                    val currentId = id.toString()
                    val idLength = currentId.length
                    var foundInvalid = false
                    for (mod in (idLength / 2) downTo 1) {
                        if (foundInvalid) break
                        val parts = mutableListOf<String>()
                        if (idLength.mod(mod) == 0) {
                            var windowStart = 0
                            var windowEnd = mod
                            while (windowEnd <= currentId.length) {
                                parts.add(currentId.substring(windowStart, windowEnd))
                                windowStart += mod
                                windowEnd += mod
                            }
                        }
                        if (parts.size > 1 && parts.all { it == parts[0] }) {
                            count += id
                            foundInvalid = true
                        }
                    }
                }
            }
            count
        }

    private suspend fun buildInput(context: android.content.Context, input: String): List<String> =
        withContext(Dispatchers.IO) {
            val input = context.assets.open(input).bufferedReader().readLine()
            input.split(",")
        }
}




