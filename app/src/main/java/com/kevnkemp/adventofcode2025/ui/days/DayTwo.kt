package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
import androidx.compose.material3.contentColorFor
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
import kotlin.math.max

class DayTwo() : Day<List<String>> {

    @Composable
    override fun Compose(modifier: Modifier) {
        val context = LocalContext.current

        var part1Duration by remember { mutableLongStateOf(0L) }
        var part2Duration by remember { mutableLongStateOf(0L) }
        var part2DurationMerged by remember { mutableLongStateOf(0L) }
        var part2DurationAI by remember { mutableLongStateOf(0L) }
        var part2DurationMid by remember { mutableLongStateOf(0L) }

        var part1Answer: Long? by remember { mutableStateOf(null) }
        var part2Answer: Long? by remember { mutableStateOf(null) }
        var part2AnswerMerged: Long? by remember { mutableStateOf(null) }
        var part2AnswerAI: Long? by remember { mutableStateOf(null) }
        var part2AnswerMid: Long? by remember { mutableStateOf(null) }

        var inputDuration by remember { mutableLongStateOf(0L) }

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val input = measureTimeWithResult {
                    buildInput(context, "aoc_25_day2.txt")
                }.also {
                    inputDuration = it.elapsedTimeMs
                }
                measureTime(
                    { part1Answer = sumInvalidIdsPart1(input.result) },
                    { part1Duration = it }
                )

                measureTime(
                    { part2Answer = sumInvalidIdsPart2(input.result) },
                    { part2Duration = it }
                )

                measureTime(
                    {
                        val mergedRanges = buildMergedRanges(input.result)
                        part2AnswerMerged = sumInvalidIdsPart2(mergedRanges)
                    },
                    { part2DurationMerged = it }
                )
                measureTime(
                    { part2AnswerAI = sumInvalidIdsPart2AI(input.result)},
                    { part2DurationAI = it }
                )
                measureTime(
                    { part2AnswerMid = sumInvalidIdsPart2Middle(input.result)},
                    { part2DurationMid = it }
                )
            }
        }
        AnswerColumn {
            InputCard(
                inputName = "Input",
                elapsedTime = { inputDuration.takeIf { it > 0L } }
            )
            AnswerCard(
                answerName = "Part 1",
                answer = { part1Answer },
                elapsedTime = { part1Duration.takeIf { part1Answer != null } },
            )
            AnswerCard(
                answerName = "Part 2",
                answer = { part2Answer },
                elapsedTime = { part2Duration.takeIf { part2Answer != null } },
            )
            AnswerCard(
                answerName = "Part 2 Merged Ranges",
                answer = { part2AnswerMerged },
                elapsedTime = { part2DurationMerged.takeIf { part2AnswerMerged != null } },
            )
            AnswerCard(
                answerName = "Part 2 AI",
                answer = { part2AnswerAI },
                elapsedTime = { part2DurationAI.takeIf { part2AnswerAI != null } },
            )
            AnswerCard(
                answerName = "Part 2 Mid",
                answer = { part2AnswerMid },
                elapsedTime = { part2DurationMid.takeIf { part2AnswerMid != null } },
            )
        }
    }

    // Part 1 Solution
    private suspend fun sumInvalidIdsPart1(input: List<String>): Long =
        withContext(Dispatchers.Default) {
            var count = 0L
            for (range in input) {
                val rangeMin = range.split("-")[0].toLong()
                val rangeMax = range.split("-")[1].toLong()

                for (i in rangeMin..rangeMax) {
                    val iString = i.toString()
                    if (iString.length % 2 == 0) {
                        val halfLength = iString.length / 2
                        val firstHalf = iString.substring(0, halfLength)
                        val secondHalf = iString.substring(halfLength)

                        if (firstHalf == secondHalf) {
                            count += i
                        }
                    }
                }
            }
            count
        }

    // Part 2 Solution
    private suspend fun sumInvalidIdsPart2(input: List<String>): Long =
        withContext(Dispatchers.Default) {
            var count = 0L
            for (range in input) {
                val rangeMin = range.split("-")[0].toLong()
                val rangeMax = range.split("-")[1].toLong()
                for (id in rangeMin..rangeMax) {
                    val currentId = id.toString()
                    val idLength = currentId.length
                    var foundInvalid = false
                    for (mod in (idLength / 2) downTo 1) {
                        if (foundInvalid) break
                        val parts = mutableListOf<String>()
                        if (idLength.mod(mod) == 0) {
                            var windowStart = 0
                            var windowEnd = mod
                            while (windowEnd <= currentId.length) {
                                parts.add(currentId.substring(windowStart, windowEnd))
                                windowStart += mod
                                windowEnd += mod
                            }
                        }
                        if (parts.size > 1 && parts.all { it == parts[0] }) {
                            count += id
                            foundInvalid = true
                        }
                    }
                }
            }
            count
        }

    private suspend fun buildMergedRanges(input: List<String>): List<String> =
        withContext(Dispatchers.Default) {
            val sorted = input.map { it.split("-")[0].toLong()..it.split("-")[1].toLong() }
                .sortedBy { it.first }
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
            merged.map { "${it.first}-${it.last}" }
        }

    override suspend fun buildInput(context: Context, input: String) =
        withContext(Dispatchers.IO) {
            val input = context.assets.open(input).bufferedReader().readLine()
            input.split(",")
        }


    private suspend fun sumInvalidIdsPart2AI(input: List<String>): Long =
        withContext(Dispatchers.Default) {
            // Parse ranges (handle input elements that might contain multiple comma-separated entries)
            val ranges = input
                .mapNotNull { piece ->
                    val p = piece.trim()
                    if (p.isEmpty()) return@mapNotNull null
                    val parts = p.split('-')
                    if (parts.size != 2) return@mapNotNull null
                    val a = parts[0].trim().toLongOrNull() ?: return@mapNotNull null
                    val b = parts[1].trim().toLongOrNull() ?: return@mapNotNull null
                    a to b
                }
                .sortedBy { it.first }
                .toMutableList()

            if (ranges.isEmpty()) return@withContext 0L

            // Merge overlapping/adjacent ranges for faster containment checks
            val merged = mutableListOf<Pair<Long, Long>>()
            var curStart = ranges.first().first
            var curEnd = ranges.first().second
            for ((s, e) in ranges.drop(1)) {
                if (s <= curEnd + 1) {
                    curEnd = max(curEnd, e)
                } else {
                    merged += curStart to curEnd
                    curStart = s
                    curEnd = e
                }
            }
            merged += curStart to curEnd

            val globalMin = merged.first().first
            val globalMax = merged.maxOf { it.second }
            val maxDigits = globalMax.toString().length

            // Precompute powers of 10
            val pow10 = LongArray(maxDigits + 1) { 1L }
            for (i in 1..maxDigits) pow10[i] = pow10[i - 1] * 10L

            // Helper: check if a number n is within any merged range using binary search on starts
            val starts = merged.map { it.first }
            fun isInAnyRange(n: Long): Boolean {
                // find index of last range whose start <= n
                var lo = 0
                var hi = starts.size - 1
                var idx = -1
                while (lo <= hi) {
                    val mid = (lo + hi) ushr 1
                    if (starts[mid] <= n) {
                        idx = mid
                        lo = mid + 1
                    } else {
                        hi = mid - 1
                    }
                }
                return if (idx >= 0) n <= merged[idx].second else false
            }

            val seen = HashSet<Long>()

            // Generate repeated-block numbers:
            // block length b from 1..maxDigits/2, repetition r from 2..(maxDigits / b)
            for (b in 1..(maxDigits / 2)) {
                val baseStart = pow10[b - 1]
                val baseEnd = pow10[b] - 1
                val maxReps = maxDigits / b
                for (r in 2..maxReps) {
                    // For fixed (b, r) the generated numbers increase with base, so we can stop early if > globalMax
                    for (base in baseStart..baseEnd) {
                        val baseStr = base.toString()
                        val s =
                            baseStr.repeat(r) // repeated string e.g. "123".repeat(3) -> "123123123"
                        // fast check: if length exceeds maxDigits skip (shouldn't happen but safe)
                        if (s.length > maxDigits) break
                        val n = s.toLongOrNull() ?: continue
                        if (n > globalMax) break // further bases only make larger numbers, break inner loop
                        if (n < globalMin) continue
                        // add only if lies in any merged range
                        if (isInAnyRange(n)) seen.add(n)
                    }
                }
            }

            // Sum unique invalid ids that fell in any range
            seen.sum()
        }

    private suspend fun sumInvalidIdsPart2Middle(input: List<String>): Long = withContext(Dispatchers.Default) {
        var sum = 0L

        // helper to parse one "a-b" string to Pair<Long,Long>, skipping invalid input
        fun parseRange(s: String): Pair<Long, Long>? {
            val trimmed = s.trim()
            if (trimmed.isEmpty()) return null
            val dash = trimmed.indexOf('-')
            if (dash <= 0) return null
            val a = trimmed.substring(0, dash).toLongOrNull() ?: return null
            val b = trimmed.substring(dash + 1).toLongOrNull() ?: return null
            return a to b
        }

        // input may contain comma-separated entries in each element
        val ranges = input
            .mapNotNull { parseRange(it) }

        for ((min, max) in ranges) {
            // iterate every id in the range (same high-level approach as original)
            for (id in min..max) {
                val s = id.toString()
                val len = s.length
                var isInvalid = false

                // try block sizes from largest possible down to 1 (same behavior as your original)
                val maxBlock = len / 2
                for (block in maxBlock downTo 1) {
                    if (len % block != 0) continue // only equal-sized blocks allowed
                    // compare the first block to each subsequent block without allocating substrings
                    var ok = true
                    var i = block
                    while (i < len) {
                        // regionMatches avoids creating s.substring(...) objects
                        if (!s.regionMatches(0, s, i, block)) {
                            ok = false
                            break
                        }
                        i += block
                    }
                    if (ok) {
                        // we found a repeated-block decomposition (e.g., "121212")
                        sum += id
                        isInvalid = true
                        break // stop trying smaller blocks for this id
                    }
                }

                // move to next id if already found invalid
                if (isInvalid) continue
            }
        }

        sum
    }

}




