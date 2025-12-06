package com.kevnkemp.adventofcode2025.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InputCard(
    inputName: String,
    elapsedTime: () -> Long?,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        elapsedTime()?.let { ms ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Column {
                    Text(text = "Time to build input for $inputName: ${ms}ms")
                }
            }
        } ?: run {
            Text(text = "Parsing $inputName...")
        }
    }
}