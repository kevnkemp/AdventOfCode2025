package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentRecomposeScope
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
import kotlinx.coroutines.withContext

class DaySix : Day<List<DaySix.Problem>> {


    @Composable
    override fun Compose(modifier: Modifier) {

        var testSolution: Long? by remember { mutableStateOf(null) }
        var testTime: Long by remember { mutableLongStateOf(0L) }

        var part1Solution: Long? by remember { mutableStateOf(null) }
        var part1Time: Long by remember { mutableLongStateOf(0L) }

        var part2Solution: Long? by remember { mutableStateOf(null) }
        var part2Time: Long by remember { mutableLongStateOf(0L) }

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(coroutineScope) {
            val testInput = buildInput(context, "aoc_25_day6_test.txt")
            val input = buildInput(context, "aoc_25_day6.txt")
            println(testInput)
            measureTime(
                { testSolution = part1(testInput) },
                { testTime = it }
            )
            measureTime(
                { part1Solution = part1(input) },
                { part1Time = it }
            )
            measureTime(
                { part2Solution = part2(testInput) },
                { part2Time = it }
            )
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
                elapsedTime = { part1Time.takeIf { part1Solution != null } },
            )
            AnswerCard(
                answerName = "Test Input Part 2",
                answer = { part2Solution },
                elapsedTime = { part2Time.takeIf { part2Solution != null } },
            )
        }
    }

    private fun part1(problems: List<Problem>): Long {
        return problems.sumOf { problem ->
            when (problem.operator) {
                Operator.ADD -> problem.numbers.sumOf { it.trim().toLong() }
                Operator.MULTIPLY -> problem.numbers.map { it.trim().toLong() }.fold(1L) { acc, i -> acc * i }
            }
        }
    }

    private fun part2(problems: List<Problem>): Long {
        var count = 0L

        for (problem in problems) {
            var currentProblemSolution = 0L
            val maxLength = problem.numbers.maxOf { it.length }
            for (i in maxLength-1 downTo 0) {
                 val currentNumber = problem.numbers.mapNotNull { it.getOrNull(i) }.joinToString(separator = "").toLong()
                println("currentNumber: $currentNumber")
                when (problem.operator) {
                    Operator.ADD -> currentProblemSolution += currentNumber
                    Operator.MULTIPLY -> {
                        if (currentProblemSolution == 0L) {
                            currentProblemSolution = 1L
                        }
                        currentProblemSolution *= currentNumber
                    }
                }
            }
            count += currentProblemSolution
        }
        return count
    }

    // num space num
    // num space+ space number
    // num space space+ num
    // num space+ space space+ num

    val regy = "(?<=(\\d|\\+|\\*)(\\s))\\s(?=(\\s)(\\d|\\+|\\*))".toRegex()
    override suspend fun buildInput(context: Context, input: String): List<Problem> = withContext(
        Dispatchers.IO
    ) {
        val problems = mutableListOf<Problem>()
        val whiteSpaceRegex = "(?<=(\\d|\\+|\\*))\\s".toRegex()
        context.assets.open(input).bufferedReader().readLines().map { line ->
            println("line: $line")
            val lineParts = line.trim().split(whiteSpaceRegex)
            if (problems.isEmpty()) {
                problems.addAll(lineParts.map { Problem(listOf(it), Operator.ADD) })
            } else {
                lineParts.forEachIndexed { index, part ->
                    if (!part.contains("+") && !part.contains("*")) {
                        problems[index] = problems[index].copy(
                            numbers = problems[index].numbers + part
                        )
                    } else {
                        problems[index] = problems[index].copy(
                            operator = when (part.trim()) {
                                "+" -> Operator.ADD
                                "*" -> Operator.MULTIPLY
                                else -> throw IllegalArgumentException("Unknown operator: $part")
                            }
                        )
                    }
                }
            }
        }
        problems
    }

    data class Problem(
        val numbers: List<String>,
        val operator: Operator,
    )

    enum class Operator {
        ADD,
        MULTIPLY
    }
}
