package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
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

class DayFour : Day {


    @Composable
    override fun Compose(modifier: Modifier) {
        val context = LocalContext.current

        var part1Solution: Int? by remember { mutableStateOf(null) }
        var testSolution: Int? by remember { mutableStateOf(null) }
        var part2Solution: Int? by remember { mutableStateOf(null) }
        var part2SolutionRecursive: Int? by remember { mutableStateOf(null) }
        var p1Time: Long by remember { mutableLongStateOf(0L) }
        var testTime: Long by remember { mutableLongStateOf(0L) }
        var p2Time: Long by remember { mutableLongStateOf(0L) }
        var p2TimeRecursive: Long by remember { mutableLongStateOf(0L) }

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            launch {
                val testGrid = buildInput<List<String>>(context, "aoc_25_day4_test.txt")
                val grid = buildInput<List<String>>(context, "aoc_25_day4.txt")
                p1Time = measureTime {
                    part1Solution = findNumberOfRollsInGrid(grid)
                }
                testTime = measureTime {
                    testSolution = findNumberOfRollsInGrid(testGrid)
                }
                p2Time = measureTime {
                    part2Solution = countAccessibleRolesLoop(grid)
                }
                p2TimeRecursive = measureTime {
                    part2SolutionRecursive= countAccessibleRollsRecursion(grid)
                }

            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            testSolution?.let { res ->
                Text(text = "Answer for test input: $res calculated in ${testTime}ms")
            } ?: run {
                Text(text = "Calculating test solution...")
            }
            part1Solution?.let { res ->
                Text(text = "Answer for Part 1: $res calculated in ${p1Time}ms")
            } ?: run {
                Text(text = "Calculating Part 1...")
            }
            part2Solution?.let { res ->
                Text(text = "Answer for Part 2: $res calculated in ${p2Time}ms")
            } ?: run {
                Text(text = "Calculating Part 2...")
            }
            part2SolutionRecursive?.let { res ->
                Text(text = "Answer for Part 2 recursive: $res calculated in ${p2TimeRecursive}ms")
            } ?: run {
                Text(text = "Calculating Part 2 recursively...")
            }
        }
    }

    private suspend fun countAccessibleRolesLoop(grid: List<String>): Int = withContext(Dispatchers.Main) {
        var count = 0
        val mutableGrid = grid.toMutableList()
        val positionsToUpdate = mutableListOf(0 to 0)
        while (positionsToUpdate.isNotEmpty()) {
            positionsToUpdate.clear()
            for (y in 0..mutableGrid.size - 1) {
                for (x in 0..mutableGrid[y].length - 1) {
                    if (mutableGrid.isCellAccessible(x = x, y = y)) {
                        count++
                        positionsToUpdate.add(x to y)
                    }
                }
            }
            for (position in positionsToUpdate) {
                val (x, y) = position
                val rowChars = mutableGrid[y].toCharArray()
                rowChars[x] = 'x'
                mutableGrid[y] = String(rowChars)
            }
        }
        count
    }

    private suspend fun countAccessibleRollsRecursion(grid: List<String>, count: Int = 0): Int = withContext(Dispatchers.Main) {
        var localCount = count
        val mutableGrid = grid.toMutableList()
        var foundAccessible = false

        for (i in 0..mutableGrid.size - 1) {
            for (j in 0..mutableGrid[i].length - 1) {
                if (mutableGrid.isCellAccessible(x = j, y = i)) {
                    localCount++
                    foundAccessible = true
                    val rowChars = mutableGrid[i].toCharArray()
                    rowChars[j] = 'x'
                    mutableGrid[i] = String(rowChars)
                }
            }
        }
        if (foundAccessible) {
            countAccessibleRollsRecursion(mutableGrid, localCount)
        } else {
            localCount
        }
    }

    private suspend fun findNumberOfRollsInGrid(grid: List<String>): Int = withContext(Dispatchers.Main) {
        var count = 0

        for (i in 0..grid.size-1) {
            for (j in 0..grid[i].length-1) {
                if (grid.isCellAccessible(x = j, y = i)) count++
            }
        }
        count
    }

    private fun List<String>.isCellAccessible(x: Int, y: Int): Boolean {
        if (this[y][x] != '@') return false
        val c = this[y].length
        val r = this.size
        val leftIndex = x - 1
        val rightIndex = x + 1
        val upIndex = y - 1
        val downIndex = y + 1

        val positonsToCheck = mutableListOf<Char>()
        if (leftIndex > -1) {
            if (upIndex > -1) {
                positonsToCheck.add(this[upIndex][leftIndex])
            }
            if (downIndex< r) {
                positonsToCheck.add(this[downIndex][leftIndex])
            }
            positonsToCheck.add(this[y][leftIndex])
        }
        if (rightIndex < c) {
            if (upIndex > -1) {
                positonsToCheck.add(this[upIndex][rightIndex])
            }
            if (downIndex < r) {
                positonsToCheck.add(this[downIndex][rightIndex])
            }
            positonsToCheck.add(this[y][rightIndex])
        }

        if (upIndex > -1) {
            positonsToCheck.add(this[upIndex][x])
        }
        if (downIndex < r) {
            positonsToCheck.add(this[downIndex][x])
        }


        val isAccessible = positonsToCheck.count {
            it == '@'
        } < 4
        return isAccessible
    }

    override suspend fun <T> buildInput(context: Context, input: String): T =
        withContext(Dispatchers.IO) {
            context.assets.open(input).bufferedReader().readLines()
        } as T
}