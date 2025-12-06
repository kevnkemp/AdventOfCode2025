package com.kevnkemp.adventofcode2025.ui.days

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface Day<T> {

    @Composable
    fun Compose(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Logic not yet implemented for ${this@Day.javaClass.simpleName}.")
        }
    }

    suspend fun CoroutineScope.measureTime(block: suspend CoroutineScope.() -> Unit): Long {
        val startTime = System.currentTimeMillis()
        this.block()
        val endTime = System.currentTimeMillis()
        return endTime - startTime
    }

    suspend fun <T> CoroutineScope.measureTimeWithResult(
        block: suspend CoroutineScope.() -> T
    ): TimedResult<T> {
        val startTime = System.currentTimeMillis()
        val result = block()
        val endTime = System.currentTimeMillis()
        return TimedResult(result, endTime - startTime)
    }

    suspend fun CoroutineScope.measureTime(
        block: suspend CoroutineScope.() -> Unit,
        onComplete: (Long) -> Unit
    ) {
        launch {
            val startTime = System.currentTimeMillis()
            block()
            val endTime = System.currentTimeMillis()
            onComplete(endTime - startTime)
        }
    }

    suspend fun buildInput(context: Context, input: String): T
}