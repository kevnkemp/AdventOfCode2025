package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

class DayOne : Day {

    @Composable
    override fun Compose(modifier: Modifier) {
        val context = LocalContext.current

        val dial = Dial(context)

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Final count for Part 1 is: ${dial.exactZeroCount}")
            Text(text = "Final count for Part 2 is: ${dial.passedZeroCount}, calc = ${dial.exactZeroCount + dial.passedZeroCount}")
        }
    }

    inner class Dial(
        context: Context,
        filePath: String = "aoc_25_day1.txt"
    ) {
        var currentPosition = 50

        var previousPosition = 0

        var exactZeroCount = 0

        var passedZeroCount = 0

        private val rotations: List<Rotation>

        init {
            rotations = buildInput(context, filePath)
            rotate()
        }

        fun rotate() {
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

        private fun buildInput(context: Context, input: String): List<Rotation> {
            val input = context.assets.open(input).bufferedReader().readLines()
            return input.mapNotNull { rotation ->
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
}


