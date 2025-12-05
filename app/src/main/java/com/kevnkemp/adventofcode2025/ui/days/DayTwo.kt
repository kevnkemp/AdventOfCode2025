package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kevnkemp.adventofcode2025.ui.common.AnswerCard
import com.kevnkemp.adventofcode2025.ui.common.AnswerColumn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DayTwo() : Day<List<String>> {
    
    @Composable
    override fun Compose(modifier: Modifier) {
        val context = LocalContext.current

        var part1Duration by remember { mutableLongStateOf(0L) }
        var part2Duration by remember { mutableLongStateOf(0L) }
        var part2DurationMerged by remember { mutableLongStateOf(0L) }

        var part1Answer: Long? by remember { mutableStateOf(null) }
        var part2Answer: Long? by remember { mutableStateOf(null) }
        var part2AnswerMerged: Long? by remember { mutableStateOf(null) }

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val input = buildInput(context, "aoc_25_day2.txt")
                part1Duration = measureTime {
                    part1Answer = sumInvalidIdsPart1(input)
                }
                part2Duration = measureTime {
                    part2Answer = sumInvalidIdsPart2(input)
                }
                part2DurationMerged = measureTime {
                    val mergedRanges = buildMergedRanges(input)
                    part2AnswerMerged = sumInvalidIdsPart2(mergedRanges)
                }
            }
        }
        AnswerColumn {
            AnswerCard(
                answerName = "Part 1",
                answer = { part1Answer },
                elapsedTime = { part1Duration.takeIf { part1Answer != null } },
            )
            AnswerCard(
                answerName = "Part 2",
                answer = { part2Answer },
                elapsedTime = { part2Duration.takeIf { part2Answer != null } },
            )
            AnswerCard(
                answerName = "Part 2 Merged Ranges",
                answer = { part2AnswerMerged},
                elapsedTime = { part2DurationMerged.takeIf { part2AnswerMerged != null } },
            )
        }
    }

    // Part 1 Solution
    private suspend fun sumInvalidIdsPart1(input: List<String>): Long =
        withContext(Dispatchers.Main) {
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
        withContext(Dispatchers.Main) {
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

    private suspend fun buildMergedRanges(input: List<String>): List<String> = withContext(Dispatchers.Main) {
        val sorted = input.map { it.split("-")[0].toLong()..it.split("-")[1].toLong() }.sortedBy { it.first }
        val merged = mutableListOf<LongRange>()

        var currentRange = sorted.first()
        for (nextRange in sorted.drop(1)) {
            if (nextRange.first <= currentRange.last) {
                val newRange = currentRange.first..maxOf(currentRange.last, nextRange.last)
                currentRange = newRange
            } else {
                merged.add(currentRange)
                currentRange = nextRange
            }
        }
        merged.add(currentRange)
        merged.map { "${it.first}-${it.last}" }
    }

    override suspend fun buildInput(context: Context, input: String) =
        withContext(Dispatchers.IO) {
            val input = context.assets.open(input).bufferedReader().readLine()
            input.split(",")
        }

}




