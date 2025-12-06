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
import com.kevnkemp.adventofcode2025.ui.days.DayThree.BatteryBank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow

class DayThree : Day<List<BatteryBank>> {

    @Composable
    override fun Compose(modifier: Modifier) {
        val context = LocalContext.current

        var part1Solution: Long? by remember { mutableStateOf(null) }
        var part1Rudimentary: Long? by remember { mutableStateOf(null) }
        var part2Solution: Long? by remember { mutableStateOf(null) }
        var p1Time: Long by remember { mutableLongStateOf(0L) }
        var p1bTime: Long by remember { mutableLongStateOf(0L) }
        var p2Time: Long by remember { mutableLongStateOf(0L) }
        var inputDuration by remember { mutableLongStateOf(0L) }


        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val batteryBanks = measureTimeWithResult {
                    buildInput(context, "aoc_25_day3.txt")
                }.also {
                    inputDuration = it.elapsedTimeMs
                }
                p1Time = measureTime {
                    part1Solution = sumBankMaxes(batteryBanks.result, 2)
                }
                p1bTime = measureTime {
                    part1Rudimentary = sumTwoDigitMaxes(batteryBanks.result,)
                }
                p2Time = measureTime {
                    part2Solution = sumBankMaxes(batteryBanks.result, 12)
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
                answer = { part1Rudimentary },
                elapsedTime = { p1bTime.takeIf { part1Rudimentary != null } },
            )
            AnswerCard(
                answerName = "Part 1",
                answer = { part1Solution },
                elapsedTime = { p1Time.takeIf { part1Solution != null } },
            )
            AnswerCard(
                answerName = "Part 2",
                answer = { part2Solution },
                elapsedTime = { p2Time.takeIf { part2Solution != null } },
            )
        }
    }

    // Part 1 Two Pointer Solution
    private suspend fun sumTwoDigitMaxes(banks: List<BatteryBank>): Long =
        withContext(Dispatchers.Default) {
            var sum = 0L
            for (bank in banks) {
                var firstMax = 0
                var secondMax = 0
                for (i in 0..<bank.digits.size - 1) {
                    val first = bank.digits[i]
                    if (first > firstMax) {
                        firstMax = first
                    } else {
                        continue
                    }
                    for (j in 1..<bank.digits.size) {
                        val second = bank.digits[j]
                        if (j != bank.digits.lastIndex && second > firstMax) {
                            firstMax = second
                            secondMax = 0
                            continue
                        } else if (second > secondMax) {
                            secondMax = second
                        }
                    }
                }
                sum += firstMax * 10 + secondMax
            }
            sum
        }


    // Part 2 Window Solution, also works for Part 1 by passing in joltageLength = 2
    private suspend fun sumBankMaxes(banks: List<BatteryBank>, joltageLength: Int): Long =
        withContext(Dispatchers.Default) {
            var sum = 0L
            for (bank in banks) {
                var pointer = 0
                val digits = bank.digits
                for (i in joltageLength-1 downTo 0) {
                    var windowMax = 0
                    for (j in pointer..<(digits.size - i)) {
                        if (digits[j] > windowMax) {
                            windowMax = digits[j]
                            pointer = j
                        }
                    }
                    sum += windowMax * 10.0.pow(i).toLong()
                    pointer++
                }
            }
            sum
        }

    override suspend fun buildInput(context: Context, input: String) =
        withContext(Dispatchers.IO) {
            val input = context.assets.open(input).bufferedReader().readLines()
            input.map { digits ->
                BatteryBank(digits = digits.mapNotNull { it.digitToInt() })
            }
        }

    data class BatteryBank(val digits: List<Int>)
}