package com.kevnkemp.adventofcode2025.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kevnkemp.adventofcode2025.ui.days.DayEight
import com.kevnkemp.adventofcode2025.ui.days.DayEleven
import com.kevnkemp.adventofcode2025.ui.days.DayEntryScreen
import com.kevnkemp.adventofcode2025.ui.days.DayFive
import com.kevnkemp.adventofcode2025.ui.days.DayFour
import com.kevnkemp.adventofcode2025.ui.days.DayNine
import com.kevnkemp.adventofcode2025.ui.days.DayOne
import com.kevnkemp.adventofcode2025.ui.days.DaySeven
import com.kevnkemp.adventofcode2025.ui.days.DaySix
import com.kevnkemp.adventofcode2025.ui.days.DayTen
import com.kevnkemp.adventofcode2025.ui.days.DayThree
import com.kevnkemp.adventofcode2025.ui.days.DayTwelve
import com.kevnkemp.adventofcode2025.ui.days.DayTwo
import kotlinx.serialization.Serializable


@Serializable object DayEntryRoute
@Serializable object DayOneRoute
@Serializable object DayTwoRoute
@Serializable object DayThreeRoute
@Serializable object DayFourRoute
@Serializable object DayFiveRoute
@Serializable object DaySixRoute
@Serializable object DaySevenRoute
@Serializable object DayEightRoute
@Serializable object DayNineRoute
@Serializable object DayTenRoute
@Serializable object DayElevenRoute
@Serializable object DayTwelveRoute
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
                    4 -> navHostController.navigate(DayFourRoute)
                    5 -> navHostController.navigate(DayFiveRoute)
                    6 -> navHostController.navigate(DaySixRoute)
                    7 -> navHostController.navigate(DaySevenRoute)
                    8 -> navHostController.navigate(DayEightRoute)
                    9 -> navHostController.navigate(DayNineRoute)
                    10 -> navHostController.navigate(DayTenRoute)
                    11 -> navHostController.navigate(DayElevenRoute)
                    12 -> navHostController.navigate(DayTwelveRoute)
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
    composable<DayFourRoute> {
        DayFour().Compose()
    }
    composable<DayFiveRoute> {
        DayFive().Compose()
    }
    composable<DaySixRoute> {
        DaySix().Compose()
    }
    composable<DaySevenRoute> {
        DaySeven().Compose()
    }
    composable<DayEightRoute> {
        DayEight().Compose()
    }
    composable<DayNineRoute> {
        DayNine().Compose()
    }
    composable<DayTenRoute> {
        DayTen().Compose()
    }
    composable<DayElevenRoute> {
        DayEleven().Compose()
    }
    composable<DayTwelveRoute> {
        DayTwelve().Compose()
    }

}