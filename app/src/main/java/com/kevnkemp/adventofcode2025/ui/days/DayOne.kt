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
import com.kevnkemp.adventofcode2025.ui.common.InputCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DayOne : Day<List<DayOne.Rotation>> {

    @Composable
    override fun Compose(modifier: Modifier) {
        val context = LocalContext.current

        val dial = Dial()

        var part1Solution: Int? by remember { mutableStateOf(null) }
        var part2Solution: Int? by remember { mutableStateOf(null) }
        var duration by remember { mutableLongStateOf(0L) }
        var inputDuration by remember { mutableLongStateOf(0L) }

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val input = measureTimeWithResult {
                    buildInput(context, "aoc_25_day1.txt")
                }.also {
                    inputDuration = it.elapsedTimeMs
                }
                duration = measureTime {
                    dial.rotate(input.result)
                    part1Solution = dial.exactZeroCount
                    part2Solution = dial.exactZeroCount + dial.passedZeroCount
                }
            }
        }

        AnswerColumn {
            InputCard(
                inputName = "Input",
                elapsedTime = { inputDuration.takeIf { it > 0L } }
            )
            AnswerCard(
                answerName = "Part 1",
                answer = { part1Solution },
                elapsedTime = { duration.takeIf { part1Solution != null } },
            )
            AnswerCard(
                answerName = "Part 2",
                answer = { part2Solution },
                elapsedTime = { duration.takeIf { part2Solution != null } },
            )
        }
    }

    inner class Dial() {
        var currentPosition = 50

        var previousPosition = 0

        // Part 1 solution variable
        var exactZeroCount = 0

        // Part 2 solution variable when summed with exactZeroCount
        var passedZeroCount = 0

        suspend fun rotate(rotations: List<Rotation>) = withContext(Dispatchers.Default) {
            for (rotation in rotations) {
                previousPosition = currentPosition
                when (rotation.direction) {
                    Direction.LEFT -> {
                        rotateLeft(rotation.clicks)
                        calculateLeftZeroPasses(rotation.clicks)
                    }

                    Direction.RIGHT -> {
                        rotateRight(rotation.clicks)
                        calculateRightZeroPasses(rotation.clicks)
                    }
                }

            }
        }

        private fun calculateRightZeroPasses(clicks: Int) {
            passedZeroCount += clicks / 100
            if (currentPosition == 0) {
                exactZeroCount++
            } else if (previousPosition != 0 && previousPosition > currentPosition) {
                passedZeroCount++
            }
        }

        private fun calculateLeftZeroPasses(clicks: Int) {
            passedZeroCount += clicks / 100
            if (currentPosition == 0) {
                exactZeroCount++
            } else if (previousPosition != 0 && previousPosition < currentPosition) {
                passedZeroCount++
            }
        }

        private fun rotateRight(clicks: Int) {
            currentPosition = (currentPosition + clicks).mod(100)
        }

        private fun rotateLeft(clicks: Int) {
            currentPosition = (currentPosition - clicks).mod(100)
        }

    }

    enum class Direction {
        LEFT, RIGHT
    }

    data class Rotation(
        val direction: Direction,
        val clicks: Int,
    ) {
        override fun toString(): String {
            val dir = when (this.direction) {
                Direction.LEFT -> "L"
                Direction.RIGHT -> "R"
            }
            return "$dir$clicks"
        }
    }

    override suspend fun buildInput(context: Context, input: String) =
        withContext(Dispatchers.IO) {
            val input = context.assets.open(input).bufferedReader().readLines()
            input.mapNotNull { rotation ->
                val direction = rotation[0]
                val clicks = rotation.substringAfter(direction).toInt()
                when (direction) {
                    'L' -> Rotation(Direction.LEFT, clicks)
                    'R' -> Rotation(Direction.RIGHT, clicks)
                    else -> {
                        println("Unknown direction: $direction")
                        null
                    }
                }
            }
        }
}


