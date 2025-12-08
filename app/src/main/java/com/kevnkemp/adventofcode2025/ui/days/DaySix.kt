package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.kevnkemp.adventofcode2025.ui.util.rememberSolutionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.text.get
import kotlin.text.set

class DaySix : Day<List<DaySix.Problem>> {

    @Composable
    override fun Compose(modifier: Modifier) {



        val part1TestSolutionA by rememberSolutionState<Long?>()
        val part1Solution by rememberSolutionState<Long?>()
        var part2TestSolution by rememberSolutionState<Long?>()
        var part2Solution by rememberSolutionState<Long?>()

        var inputTime: Long by remember { mutableLongStateOf(0L) }
        var inputTimeV2: Long by remember { mutableLongStateOf(0L) }

        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(coroutineScope) {
            val testInput = buildInput(context, "aoc_25_day6_test.txt")
            val inputResult = measureTimeWithResult {
                buildInput(context, "aoc_25_day6.txt")
            }.also {
                inputTime = it.elapsedTimeMs
            }
            val inputResultV2 = measureTimeWithResult {
                buildInputV2(context, "aoc_25_day6.txt")
            }.also {
                inputTimeV2 = it.elapsedTimeMs
            }

            measureTime(
                { part1TestSolutionA.result(part1(testInput)) },
                { part1TestSolutionA.time(it) }
            )
            measureTime(
                { part1Solution.result(part1(inputResult.result))},
                { part1Solution.time(it) }
            )
            measureTime(
                { part2TestSolution.result(part2(testInput)) },
                { part2TestSolution.time(it) }
            )
            measureTime(
                { part2Solution.result(part2(inputResult.result)) },
                { part2Solution.time(it) }
            )
        }
        AnswerColumn {
            InputCard(
                inputName = "Input Time",
                elapsedTime = { inputTime },
            )
            InputCard(
                inputName = "Input Time V2",
                elapsedTime = { inputTimeV2 },
            )
            AnswerCard(
                answerName = "Test Input Part 1",
                answer = { part1TestSolutionA.result },
                elapsedTime = { part1TestSolutionA.elapsedTimeMs },
            )
            AnswerCard(
                answerName = "Part 1",
                answer = { part1Solution.result },
                elapsedTime = { part1Solution.elapsedTimeMs },
            )
            AnswerCard(
                answerName = "Test Input Part 2",
                answer = { part2TestSolution.result },
                elapsedTime = { part2TestSolution.elapsedTimeMs },
            )
            AnswerCard(
                answerName = "Test Input Part 2",
                answer = { part2Solution.result },
                elapsedTime = { part2Solution.elapsedTimeMs },
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
                 val currentNumber = problem.numbers.mapNotNull { it.getOrNull(i) }.joinToString(separator = "").trim().toLong()
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

    override suspend fun buildInput(context: Context, input: String): List<Problem> = withContext(
        Dispatchers.IO
    ) {
        val problems = mutableListOf<Problem>()
        val lines = context.assets.open(input).bufferedReader().readLines()
        lines.last().forEachIndexed { i, c ->
            if (c == '*' || c == '+') {
                c.toOperator()?.let { op ->
                    problems.add(Problem(operator = op, startIndex = i))
                }
            }
        }
        for (line in lines.dropLast(1)) {
            for (i in 0..<problems.size) {
                if (i == problems.size - 1) {
                    problems[i]  = problems[i].copy(
                        numbers = problems[i].numbers + line.subSequence(problems[i].startIndex, line.length).toString()
                    )
                } else {
                    problems[i] = problems[i].copy(
                        numbers = problems[i].numbers + line.subSequence(
                            problems[i].startIndex,
                            problems[i + 1].startIndex - 1
                        ).toString()
                    )
                }
            }
        }
        problems
    }

    suspend fun buildInputV2(context: Context, input: String): List<Problem> = withContext(
        Dispatchers.IO
    ) {
        context.assets.open(input).bufferedReader().useLines { lines ->
            val linesList = lines.toList()
            val operatorLine = linesList.last()

            // Build problems with operators and indices
            val problems = operatorLine.mapIndexedNotNull { index, char ->
                char.toOperator()?.let { operator ->
                    Problem(operator = operator, startIndex = index)
                }
            }.toMutableList()

            // Extract end indices for each problem
            val ranges = problems.mapIndexed { i, problem ->
                val endIndex = problems.getOrNull(i + 1)?.startIndex?.minus(1) ?: operatorLine.length
                problem.startIndex to endIndex
            }

            // Process number lines once, building up each problem's numbers
            linesList.dropLast(1).forEach { line ->
                ranges.forEachIndexed { i, (start, end) ->
                    problems[i] = problems[i].copy(
                        numbers = problems[i].numbers + line.substring(start, minOf(end, line.length))
                    )
                }
            }

            problems
        }
    }

    fun Char.toOperator(): Operator? {
        return when (this) {
            '+' -> Operator.ADD
            '*' -> Operator.MULTIPLY
            else -> null
        }
    }

    data class Problem(
        val numbers: List<String> = listOf(),
        val operator: Operator,
        val startIndex: Int,
    )

    enum class Operator {
        ADD,
        MULTIPLY
    }
}
