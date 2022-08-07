package ru.klekchyan.favorites_ui

import androidx.compose.material.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.klekchyan.easytrip.navigation.FavoritesNavigationGraph

fun NavGraphBuilder.addFavorites(navHostController: NavHostController) {
    navigation(
        startDestination = FavoritesNavigationGraph.AllFavorites.route,
        route = FavoritesNavigationGraph.Root.route
    ) {
        composable(FavoritesNavigationGraph.AllFavorites.route) {
            Text(text = "All favorites")
        }

        composable(FavoritesNavigationGraph.SpecificFavorite().route) {
            Text(text = "Specific favorite")
        }
    }
}