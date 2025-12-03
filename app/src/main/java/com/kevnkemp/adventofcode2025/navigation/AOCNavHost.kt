package com.kevnkemp.adventofcode2025.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kevnkemp.adventofcode2025.ui.days.DayEntryScreen
import com.kevnkemp.adventofcode2025.ui.days.DayOne
import com.kevnkemp.adventofcode2025.ui.days.DayThree
import com.kevnkemp.adventofcode2025.ui.days.DayTwo
import kotlinx.serialization.Serializable


@Serializable
object DayEntryRoute
@Serializable
object DayOneRoute
@Serializable
object DayTwoRoute
@Serializable
object DayThreeRoute
@Composable
fun AOCNavHost(
    navHostController: NavHostController = rememberNavController(),
    modifier: Modifier,
) {

    NavHost(navController = navHostController, startDestination = DayEntryRoute, modifier = modifier) {
        dayEntryNavGraph(navHostController = navHostController)
    }
}

fun NavGraphBuilder.dayEntryNavGraph(
    navHostController: NavHostController,
) {
    composable<DayEntryRoute> {
        DayEntryScreen(
            onNavigateToDay = {
                when (it) {
                    1 -> navHostController.navigate(DayOneRoute)
                    2 -> navHostController.navigate(DayTwoRoute)
                    3 -> navHostController.navigate(DayThreeRoute)

                }
            },
        )
    }

    composable<DayOneRoute> {
        DayOne().Compose()
    }
    composable<DayTwoRoute> {
        DayTwo().Compose()
    }
    composable<DayThreeRoute> {
        DayThree().Compose()
    }
}