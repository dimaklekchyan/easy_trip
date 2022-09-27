package ru.klekchyan.favorites_ui

import androidx.compose.material.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.klekchyan.easytrip.navigation.FavoritesNavigationGraph
import ru.klekchyan.favorites_ui.screen.AllFavoritesScreen

fun NavGraphBuilder.addFavorites(navHostController: NavHostController) {
    navigation(
        startDestination = FavoritesNavigationGraph.AllFavorites.route,
        route = FavoritesNavigationGraph.Root.route
    ) {
        composable(FavoritesNavigationGraph.AllFavorites.route) {
            AllFavoritesScreen(vm = hiltViewModel())
        }

        composable(FavoritesNavigationGraph.SpecificFavorite().route) {
            Text(text = "Specific favorite")
        }
    }
}