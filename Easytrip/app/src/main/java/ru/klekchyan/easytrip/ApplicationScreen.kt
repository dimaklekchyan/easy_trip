package ru.klekchyan.easytrip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ru.klekchyan.easytrip.base_ui.components.AppBottomNavigation
import ru.klekchyan.easytrip.base_ui.components.AppSystemBars
import ru.klekchyan.easytrip.main_ui.addMain
import ru.klekchyan.easytrip.navigation.MainNavigationGraph
import ru.klekchyan.favorites_ui.addFavorites

@Composable
fun ApplicationScreen() {
    val navController = rememberNavController()

    AppSystemBars()

    Scaffold(
        bottomBar = {
            if(true) {
                AppBottomNavigation(navController = navController)
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(it)
        ) {
            NavHost(navController = navController, startDestination = MainNavigationGraph.Root.route) {
                addMain(navController)
                addFavorites(navController)
            }
        }
    }
}