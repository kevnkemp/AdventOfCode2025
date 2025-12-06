package com.kevnkemp.adventofcode2025.ui.common

import android.content.ClipData
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AnswerCard(
    answerName: String,
    answer: () -> Any?,
    elapsedTime: () -> Long?,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboard.current
    val coroutineScope = rememberCoroutineScope()
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        answer()?.let { res ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(2.5f),
                ) {
                    FlowRow {
                        Text(text = "Answer for $answerName: ")
                        Text(text = "$res", fontWeight = FontWeight.Bold)
                    }
                    elapsedTime()?.let { time ->
                        Text(text = "${time}ms")
                    }
                }
                Spacer(modifier = Modifier.weight(0.5f))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        coroutineScope.launch {
                            clipboardManager.setClipEntry(
                                ClipEntry(
                                    clipData = ClipData.newPlainText(
                                        res.toString(),
                                        res.toString()
                                    )
                                )
                            )
                        }
                    }
                ) {
                    Text(text = "Copy")
                }
            }
        } ?: run {
            Text(text = "Calculating $answerName...")
        }
    }

}