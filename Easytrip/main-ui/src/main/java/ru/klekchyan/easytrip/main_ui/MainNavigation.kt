package ru.klekchyan.easytrip.main_ui

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.klekchyan.easytrip.main_ui.screen.MainScreen
import ru.klekchyan.easytrip.navigation.MainNavigationGraph

fun NavGraphBuilder.addMain(navHostController: NavHostController) {
    navigation(
        startDestination = MainNavigationGraph.MapScreen.route,
        route = MainNavigationGraph.Root.route
    ) {
        composable(MainNavigationGraph.MapScreen.route) {
            MainScreen(vm = hiltViewModel())
        }
    }
}