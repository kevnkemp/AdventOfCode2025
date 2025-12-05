package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kevnkemp.adventofcode2025.ui.common.AnswerCard
import com.kevnkemp.adventofcode2025.ui.common.AnswerColumn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DayFour : Day<MutableMap<Int, MutableList<Char>>> {


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

        LaunchedEffect(Unit) {
            launch {
                val testGrid = buildInput(context, "aoc_25_day4_test.txt")
                val grid = buildInput(context, "aoc_25_day4.txt")
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
                    part2SolutionRecursive = countAccessibleRollsRecursion(grid)
                }

            }
        }

        AnswerColumn {
            AnswerCard(
                answerName = "Test Input",
                answer = { testSolution },
                elapsedTime = { testTime.takeIf { testSolution != null } },
            )
            AnswerCard(
                answerName = "Part 1",
                answer = { part1Solution },
                elapsedTime = { p1Time.takeIf { part1Solution != null } },
            )
            AnswerCard(
                answerName = "Part 1",
                answer = { part2Solution },
                elapsedTime = { p2Time.takeIf { part2Solution != null } },
            )
            AnswerCard(
                answerName = "Part 1",
                answer = { part2SolutionRecursive },
                elapsedTime = { p2TimeRecursive.takeIf { part2SolutionRecursive != null } },
            )
        }
    }

    private suspend fun countAccessibleRolesLoop(grid: MutableMap<Int, MutableList<Char>>): Int = withContext(Dispatchers.Main) {
        var count = 0
        val positionsToUpdate = mutableListOf(0 to 0)
        while (positionsToUpdate.isNotEmpty()) {
            positionsToUpdate.clear()
            for (y in 0..grid.size - 1) {
                for (x in 0..(grid[y]?.size?.minus(1) ?: 0)) {
                    if (grid.isCellAccessible(x = x, y = y)) {
                        count++
                        positionsToUpdate.add(x to y)
                    }
                }
            }
            for (position in positionsToUpdate) {
                val (x, y) = position
                grid[y]?.set(x, 'x')
            }
        }
        count
    }

    private suspend fun countAccessibleRollsRecursion(
        grid: MutableMap<Int, MutableList<Char>>,
        x: Int = 0,
        y: Int = 0,
    ): Int = withContext(Dispatchers.Main) {
        if (!grid.isCellAccessible(x = x, y = y)) return@withContext 0

        grid[y]?.set(x, 'x')
        var count = 1

        val c = grid[y]?.size ?: 0
        val r = grid.size

        // Check all 8 neighboring cells
        val neighbors = listOf(
            x - 1 to y - 1,  // top-left
            x to y - 1,      // top
            x + 1 to y - 1,  // top-right
            x - 1 to y,      // left
            x + 1 to y,      // right
            x - 1 to y + 1,  // bottom-left
            x to y + 1,      // bottom
            x + 1 to y + 1   // bottom-right
        )

        for ((nx, ny) in neighbors) {
            if (nx in 0..<c && ny in 0..<r) {
                count += countAccessibleRollsRecursion(grid, nx, ny)
            }
        }
        count
    }

    private suspend fun findNumberOfRollsInGrid(grid: MutableMap<Int, MutableList<Char>>): Int = withContext(Dispatchers.Main) {
        var count = 0

        for (i in 0..grid.size - 1) {
            for (j in 0..(grid[i]?.size?.minus(1) ?: 0)) {
                if (grid.isCellAccessible(x = j, y = i)) count++
            }
        }
        count
    }

    private fun MutableMap<Int, MutableList<Char>>.isCellAccessible(x: Int, y: Int): Boolean {
        if (this[y]?.get(x) != '@') return false
        val c = this[y]?.size ?: 0
        val r = this.size
        val leftIndex = x - 1
        val rightIndex = x + 1
        val upIndex = y - 1
        val downIndex = y + 1

        val positonsToCheck = mutableListOf<Char>()
        if (leftIndex > -1) {
            if (upIndex > -1) {
                this[upIndex]?.get(leftIndex)?.let {
                    positonsToCheck.add(it)
                }
            }
            if (downIndex< r) {
                this[downIndex]?.get(leftIndex)?.let {
                    positonsToCheck.add(it)
                }
            }
            this[y]?.get(leftIndex)?.let {
                positonsToCheck.add(it)
            }
        }
        if (rightIndex < c) {
            if (upIndex > -1) {
                this[upIndex]?.get(rightIndex)?.let {
                    positonsToCheck.add(it)
                }
            }
            if (downIndex < r) {
                this[downIndex]?.get(rightIndex)?.let {
                    positonsToCheck.add(it)
                }
            }
            this[y]?.get(rightIndex)?.let {
                positonsToCheck.add(it)
            }
        }

        if (upIndex > -1) {
            this[upIndex]?.get(x)?.let {
                positonsToCheck.add(it)
            }
        }
        if (downIndex < r) {
            this[downIndex]?.get(x)?.let {
                positonsToCheck.add(it)
            }
        }


        val isAccessible = positonsToCheck.count {
            it == '@'
        } < 4
        return isAccessible
    }

    override suspend fun buildInput(context: Context, input: String) =
        withContext(Dispatchers.IO) {
            val map = mutableMapOf<Int, MutableList<Char>>()
            context.assets.open(input).bufferedReader().readLines().mapIndexed { index, line ->
                map.put(index, line.map { it }.toMutableList())
            }
            map
        }
}