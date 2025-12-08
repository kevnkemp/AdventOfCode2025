package com.kevnkemp.adventofcode2025.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class SolutionState<T>(
    initialResult: T? = null,
) {
    var result: T? by mutableStateOf(initialResult)
        private set

    var elapsedTimeMs: Long? by mutableStateOf(null)
        private set

    fun result(value: T?) {
        result = value
    }

    fun time(value: Long) {
        elapsedTimeMs = value
    }

    fun set(result: T?, elapsedTimeMs: Long) {
        this.result = result
        this.elapsedTimeMs = elapsedTimeMs
    }
}

@Composable
fun <T> rememberSolutionState(): MutableState<SolutionState<T>> {
    return remember { mutableStateOf(SolutionState()) }
}