package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
import androidx.compose.material3.contentColorFor
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.sqrt

class DayEight : Day<List<DayEight.Coordinate>> {


    @Composable
    override fun Compose(modifier: Modifier) {

        val part1TestSolution by rememberSolutionState<Long?>()
        val part1Solution by rememberSolutionState<Long?>()
        var part2TestSolution by rememberSolutionState<Long?>()
        var part2Solution by rememberSolutionState<Long?>()

        var testInputTime: Long by remember { mutableLongStateOf(0L) }
        var inputTime: Long by remember { mutableLongStateOf(0L) }


        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(coroutineScope) {
            coroutineScope.launch {
                val (testInput, testTime) = measureTimeWithResult {
                    buildInput(context, "aoc_25_day8_test.txt")
                }
                testInputTime = testTime
                val (input, time) = measureTimeWithResult {
                    buildInput(context, "aoc_25_day8.txt")
                }
                inputTime = time
                measureTime(
                    { part1TestSolution.result(part1(testInput, 10)) },
                    { part1TestSolution.time(it) },
                )

                measureTime(
                    { part1Solution.result(part1(input, 1000)) },
                    { part1Solution.time(it) },
                )

                measureTime(
                    { part2TestSolution.result(part2(testInput)) },
                    { part2TestSolution.time(it) },
                )

                measureTime(
                    { part2Solution.result(part2(input)) },
                    { part2Solution.time(it) },
                )
            }
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
        }
    }

    private suspend fun part1(input: List<Coordinate>, iterations: Int): Long =
        withContext(Dispatchers.Default) {
            val connections = buildConnections(input)
            val circuitSet = mutableListOf(mutableSetOf<Coordinate>())
            for (i in 0 until iterations) {
                val from = connections[i].from
                val to = connections[i].to

                val foundCircuits = circuitSet.mapIndexedNotNull { index, set ->
                    if (set.contains(from) || set.contains(to)) index else null
                }
                when {
                    foundCircuits.size == 1 -> {
                        val foundCircuitIndex = foundCircuits.first()
                        circuitSet[foundCircuitIndex].add(from)
                        circuitSet[foundCircuitIndex].add(to)
                    }

                    foundCircuits.size > 1 -> {
                        val newSet = mutableSetOf<Coordinate>()
                        foundCircuits.forEach { circuitIndex ->
                            newSet += circuitSet[circuitIndex]
                        }
                        foundCircuits.sortedByDescending { it }.forEach { circuitIndex ->
                            circuitSet.removeAt(circuitIndex)
                        }
                        circuitSet.add(newSet)
                    }

                    else -> {
                        circuitSet.add(mutableSetOf(from, to))
                    }
                }
            }

            val sortedSet = circuitSet.sortedBy { it.size }.reversed()
//            printCircuitSet(sortedSet)
            sortedSet.take(3).map { it.size.toLong() }.reduce { acc, i -> acc * i }

        }

    private suspend fun part2(input: List<Coordinate>): Long =
        withContext(Dispatchers.Default) {
            val connections = buildConnections(input)
            val circuitSet = mutableListOf<MutableSet<Coordinate>>()
            var i = 0
            var from = connections[0].from
            var to = connections[0].to
            while ((circuitSet.getOrNull(0)?.size ?: 0) < input.size) {
                from = connections[i].from
                to = connections[i].to

                val foundCircuits = circuitSet.mapIndexedNotNull { index, set ->
                    if (set.contains(from) || set.contains(to)) index else null
                }
                when {
                    foundCircuits.size == 1 -> {
                        val foundCircuitIndex = foundCircuits.first()
                        circuitSet[foundCircuitIndex].add(from)
                        circuitSet[foundCircuitIndex].add(to)
                    }

                    foundCircuits.size > 1 -> {
                        val newSet = mutableSetOf<Coordinate>()
                        foundCircuits.forEach { circuitIndex ->
                            newSet += circuitSet[circuitIndex]
                        }
                        foundCircuits.sortedByDescending { it }.forEach { circuitIndex ->
                            circuitSet.removeAt(circuitIndex)
                        }
                        circuitSet.add(newSet)
                    }

                    else -> {
                        circuitSet.add(mutableSetOf(from, to))
                    }
                }
                i++
            }

            from.x.toLong() * to.x.toLong()

        }

    private suspend fun buildConnections(input: List<Coordinate>): List<Connection> = withContext(
        Dispatchers.Default
    ) {
        val connections = mutableListOf<Connection>()
        for (i in 0 until input.size) {
            val coordA = input[i]
            for (j in i + 1 until input.size) {
                val coordB = input[j]
                val distance = calculateDistance(coordA, coordB)
                connections.add(Connection(coordA, coordB, distance))
            }
        }
        connections.sortedBy { it.distance }
    }

    private fun printCircuitSet(circuitSet: List<Set<Coordinate>>) {
        println("Circuit Set:")
        for ((index, circuit) in circuitSet.withIndex()) {
            println("  Circuit $index, Size ${circuit.size} - $circuit")
        }
    }

    private fun calculateDistance(coordinateA: Coordinate, coordinateB: Coordinate): Double {
        val dx = (coordinateA.x - coordinateB.x).toDouble().pow(2)
        val dy = (coordinateA.y - coordinateB.y).toDouble().pow(2)
        val dz = (coordinateA.z - coordinateB.z).toDouble().pow(2)
        return sqrt(dx + dy + dz)
    }

    override suspend fun buildInput(context: Context, input: String) = withContext(Dispatchers.IO) {
        context.assets.open(input).bufferedReader().useLines { lines ->
            lines.map { line ->
                val (x, y, z) = line.split(",").map { it.toInt() }
                Coordinate(x, y, z)
            }.toList()
        }
    }


    data class Coordinate(val x: Int, val y: Int, val z: Int)

    data class Connection(
        val from: Coordinate,
        val to: Coordinate,
        val distance: Double,
    ) {
        override fun equals(other: Any?): Boolean {
            return when (val obj = other) {
                is Connection -> {
                    ((from == obj.from && to == obj.to) || (from == obj.to && to == obj.from)) && distance == obj.distance
                }

                else -> false
            }
        }

        override fun hashCode(): Int {
            var result = distance.hashCode()
            result = 31 * result + from.hashCode()
            result = 31 * result + to.hashCode()
            return result
        }
    }

}