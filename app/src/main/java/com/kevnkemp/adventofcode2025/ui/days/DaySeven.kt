package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kevnkemp.adventofcode2025.ui.common.AnswerCard
import com.kevnkemp.adventofcode2025.ui.common.AnswerColumn
import com.kevnkemp.adventofcode2025.ui.common.InputCard
import com.kevnkemp.adventofcode2025.ui.util.rememberSolutionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DaySeven : Day<DaySeven.TachyonDiagram> {

    @Composable
    override fun Compose(modifier: Modifier) {


        var part1TestSolution by rememberSolutionState<Int?>()
        val part1Solution by rememberSolutionState<Int?>()
        var part2TestSolution by rememberSolutionState<Int?>()
        var part2Solution by rememberSolutionState<Int?>()
        var part1And2Solution by rememberSolutionState<Int?>()

        var testInputTime: Long by remember { mutableLongStateOf(0L) }
        var inputTime: Long by remember { mutableLongStateOf(0L) }

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(coroutineScope) {
            val (testInput, testTime) = measureTimeWithResult {
                buildInput(context, "aoc_25_day7_test.txt")
            }
            val (input, time) = measureTimeWithResult {
                buildInput(context, "aoc_25_day7.txt")
            }
            testInputTime = testTime
            inputTime = time
            measureTime(
                {
                    part1TestSolution.result(
                        part1(
                            rows = testInput.manifoldRows.drop(1),
                            initialBeamIndex = testInput.manifoldRows[0].indexOf('S'),
                        )
                    )
                },
                { part1TestSolution.time(it) }
            )
            measureTime(
                {
                    part1Solution.result(
                        part1(
                            rows = input.manifoldRows.drop(1),
                            initialBeamIndex = input.manifoldRows[0].indexOf('S'),
                        )
                    )
                },
                { part1Solution.time(it) }
            )

            measureTime(
                {
                    part2TestSolution.result(
                        part1And2(
                            testInput.manifoldRows // .drop(1),
//                            initialBeamIndex = testInput.manifoldRows[0].indexOf('S'),
//                            timelines = 0
                        )
                    )
                },
                { part2TestSolution.time(it) }
            )
            measureTime(
                {
                    part2Solution.result(
                        part2(
                            rows = input.manifoldRows.drop(1),
                            initialBeamIndex = input.manifoldRows[0].indexOf('S'),
                            timelines = 0
                        )
                    )
                },
                { part2Solution.time(it) }
            )

            measureTime(
                {
                   part1And2Solution.result(part1And2(input.manifoldRows))
                },
                {
                    part1And2Solution.time(it)
                }
            )
        }

        AnswerColumn(modifier = modifier) {
            InputCard(
                inputName = "Test Input",
                elapsedTime = { testInputTime.takeIf { it > 0L } }
            )
            InputCard(
                inputName = "Test Input",
                elapsedTime = { inputTime.takeIf { it > 0L } }
            )
            AnswerCard(
                answerName = "Part 1 Test Solution",
                answer = { part1TestSolution.result },
                elapsedTime = { part1TestSolution.elapsedTimeMs },
            )
            AnswerCard(
                answerName = "Part 1 Solution",
                answer = { part1Solution.result },
                elapsedTime = { part1Solution.elapsedTimeMs },
            )
            AnswerCard(
                answerName = "Part 2 Test Solution",
                answer = { part2TestSolution.result },
                elapsedTime = { part2TestSolution.elapsedTimeMs },
            )
            AnswerCard(
                answerName = "Part 2 Solution",
                answer = { part2Solution.result },
                elapsedTime = { part2Solution.elapsedTimeMs },
            )
            AnswerCard(
                answerName = "Part 1 & 2 Solution",
                answer = { part1And2Solution.result },
                elapsedTime = { part1And2Solution.elapsedTimeMs },
            )


        }
    }

    private fun part1(rows: List<CharArray>, initialBeamIndex: Int): Int {
       val beamIndices = mutableSetOf(initialBeamIndex)
        var splitCount = 0
        var timelines = 0
        var totalBeams = 0
        var totalBeamsAdd = 0
        var totalBeamsRemove = 0

        for (row in rows) {
            val beamIndicesToRemove = mutableListOf<Int>()
            val beamIndicesToAdd = mutableListOf<Int>()
            for (beamIndex in beamIndices) {
                if (row[beamIndex] == '^') {
                    beamIndicesToRemove.add(beamIndex)
                    if (beamIndex > 0) {
                        beamIndicesToAdd.add(beamIndex - 1)
                        timelines++
                    }
                    if (beamIndex < row.size - 1) {
                        beamIndicesToAdd.add(beamIndex + 1)
                        timelines++
                    }
//                    println("Beam at index $beamIndex split, $beamIndices")
                    splitCount++
                }
            }
            beamIndices.removeAll(beamIndicesToRemove)
            totalBeamsRemove += beamIndicesToRemove.size
            beamIndices.addAll(beamIndicesToAdd.distinct())
            totalBeamsAdd += beamIndicesToAdd.size
            val updatedRowToPrint = row.toMutableList()
            totalBeams += beamIndices.size

            for (index in beamIndices) {
                updatedRowToPrint[index] = '|'
            }
//            for (c in updatedRowToPrint) {
//                print(c)
//            }
//            println()
        }
        println("totalBeams: ${totalBeams-1}")
        println("totalBeamsAdd: $totalBeamsAdd")
        println("totalBeamsRemove: $totalBeamsRemove")
        println("timelines: $timelines")
        return splitCount
    }

    fun part1And2(lines: List<CharArray>): Int {
        var currentRow = lines[0].map { if (it == '.') 0 else 1 }
        var currentRowPart2 = lines[0].map { if (it == '.') 0L else 1L }
        var numSplits = 0

        for (line in lines.drop(1)) {
            val nextRow = MutableList(line.size) { 0 }
            val nextRowPart2 = MutableList(line.size) { 0L }

            for ((i, value) in line.withIndex()) {
                if (value == '^') {
                    if (currentRow[i] > 0) {
                        nextRow[i - 1] = 1
                        nextRow[i + 1] = 1
                        numSplits++
                        nextRowPart2[i - 1] += currentRowPart2[i]
                        nextRowPart2[i + 1] += currentRowPart2[i]
                    }
                } else {
                    // carry forward existing beams
                    nextRow[i] = maxOf(nextRow[i], currentRow[i])
                    nextRowPart2[i] += currentRowPart2[i]
                }
            }

            currentRow = nextRow
            currentRowPart2 = nextRowPart2
        }

        println("number of splits is $numSplits")
        println("the number of timelines is ${currentRowPart2.sum()}")
        return numSplits
    }

    fun part2(rows: List<CharArray>, initialBeamIndex: Int, timelines: Int): Int {
        return 0
    }


    override suspend fun buildInput(context: Context, input: String) = withContext(Dispatchers.IO) {
        val list = context.assets.open(input).bufferedReader().readLines().map {
            it.toCharArray()
        }
        TachyonDiagram(list)
    }


    data class TachyonDiagram(
        val manifoldRows: List<CharArray>
    )
}

// 2091