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
import kotlin.math.pow

class DayThree : Day {

    @Composable
    override fun Compose(modifier: Modifier) {
        val context = LocalContext.current

        var part1Solution: Long? by remember { mutableStateOf(null) }
        var part1Rudimentary: Long? by remember { mutableStateOf(null) }
        var part2Solution: Long? by remember { mutableStateOf(null) }
        var p1Time: Long by remember { mutableLongStateOf(0L) }
        var p1bTime: Long by remember { mutableLongStateOf(0L) }
        var p2Time: Long by remember { mutableLongStateOf(0L) }

        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val batteryBanks = buildInput<List<BatteryBank>>(context, "aoc_25_day3.txt")
                p1Time = measureTime {
                    part1Solution = sumBankMaxes(batteryBanks, 2)
                }
                p1bTime = measureTime {
                    part1Rudimentary = sumTwoDigitMaxes(batteryBanks,)
                }
                p2Time = measureTime {
                    part2Solution = sumBankMaxes(batteryBanks, 12)
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            part1Rudimentary?.let { res ->
                Text(text = "Answer for Part 1 rudimentary: $res calculated in ${p1bTime}ms")
            } ?: run {
                Text(text = "Calculating Part 1b...")
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

    override suspend fun <T> buildInput(context: Context, input: String): T =
        withContext(Dispatchers.IO) {
            val input = context.assets.open(input).bufferedReader().readLines()
            input.map { digits ->
                BatteryBank(digits = digits.mapNotNull { it.digitToInt() })
            } as T
        }

    data class BatteryBank(val digits: List<Int>)
}