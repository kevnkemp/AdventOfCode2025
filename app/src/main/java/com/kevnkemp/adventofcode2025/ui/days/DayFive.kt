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

class DayFive : Day<DayFive.IngredientDatabase> {

    @Composable
    override fun Compose(modifier: Modifier) {
        val context = LocalContext.current

        var part1Solution: Int? by remember { mutableStateOf(null) }
        var part1SolutionOpt: Int? by remember { mutableStateOf(null) }
        var testSolution: Int? by remember { mutableStateOf(null) }
        var part2Solution: Long? by remember { mutableStateOf(null) }
        var p1Time: Long by remember { mutableLongStateOf(0L) }
        var p1TimeOpt: Long by remember { mutableLongStateOf(0L) }
        var testTime: Long by remember { mutableLongStateOf(0L) }
        var p2Time: Long by remember { mutableLongStateOf(0L) }

        LaunchedEffect(Unit) {
            launch {
                val testIngredientDatabase = buildInput(context, "aoc_25_day5_test.txt")
                val ingredientDatabase = buildInput(context, "aoc_25_day5.txt")
                p1Time = measureTime {
                    part1Solution = countFreshIngredients(ingredientDatabase)
                }
                p1TimeOpt = measureTime {
                    part1SolutionOpt = part1Optimized(ingredientDatabase)
                }

                testTime = measureTime {
                    testSolution = countFreshIngredients(testIngredientDatabase)
                }
                p2Time = measureTime {
                    part2Solution = countTotal(ingredientDatabase)
                }

            }
        }

        AnswerColumn {
            AnswerCard(
                answerName = "Test Input Part 1",
                answer = { testSolution },
                elapsedTime = { testTime.takeIf { testSolution != null } },
            )
            AnswerCard(
                answerName = "Part 1",
                answer = { part1Solution },
                elapsedTime = { p1Time.takeIf { part1Solution != null } },
            )
            AnswerCard(
                answerName = "Part 1 Optimized",
                answer = { part1SolutionOpt },
                elapsedTime = { p1TimeOpt.takeIf { part1SolutionOpt != null } },
            )
            AnswerCard(
                answerName = "Part 2",
                answer = { part2Solution },
                elapsedTime = { p2Time.takeIf { part2Solution != null } },
            )
        }
    }

    private fun countFreshIngredients(ingredientDatabase: IngredientDatabase): Int {
        var count = 0
        for (ingredientId in ingredientDatabase.availableIngredientIds) {
            for (range in ingredientDatabase.freshRanges) {
                if (ingredientId in range) {
                    count++
                    break
                }
            }
        }
        return count
    }

    private fun part1Optimized(ingredientDatabase: IngredientDatabase): Int {
        val merged = buildMergedRanges(ingredientDatabase)
        var count = 0
        for (ingredientId in ingredientDatabase.availableIngredientIds) {
            for (range in merged) {
                if (ingredientId in range) {
                    count++
                    break
                }
            }
        }
        return count
    }


    private fun countTotal(ingredientDatabase: IngredientDatabase): Long {
        val merged = buildMergedRanges(ingredientDatabase)
        return merged.sumOf { range ->
            range.last - range.first + 1
        }
    }

    private fun buildMergedRanges(ingredientDatabase: IngredientDatabase): List<LongRange> {
        val sorted = ingredientDatabase.freshRanges.sortedBy { it.first }.toMutableList()
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
        return merged
    }

    override suspend fun buildInput(context: Context, input: String) =
        withContext(Dispatchers.IO) {
            var parsingFreshIngredients = true
            val freshIngredients = mutableListOf<LongRange>()
            val availableIngredients = mutableListOf<Long>()

            context.assets.open(input).bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    when {
                        line.isEmpty() -> parsingFreshIngredients = false
                        parsingFreshIngredients -> freshIngredients.add(line.toIntRange())
                        else -> availableIngredients.add(line.toLong())
                    }
                }
            }

            IngredientDatabase(
                freshRanges = freshIngredients,
                availableIngredientIds = availableIngredients,
            )
        }

    private fun String.toIntRange(): LongRange {
        val parts = this.split("-")
        return parts[0].toLong()..parts[1].toLong()
    }

    data class IngredientDatabase(
        val freshRanges: List<LongRange>,
        val availableIngredientIds: List<Long>,
    )
}


