package com.kevnkemp.adventofcode2025.ui.days

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kevnkemp.adventofcode2025.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayEntryScreen(modifier: Modifier = Modifier, onNavigateToDay: (Int) -> Unit) {

        Column(
            modifier = modifier
                .padding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (day in 1..12) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNavigateToDay(day)
                        }
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Navigate to Day $day Screen"
                    )
                }
            }
        }

}