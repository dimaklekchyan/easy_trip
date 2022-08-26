package ru.klekchyan.easytrip.base_ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.klekchyan.easytrip.base_ui.R
import ru.klekchyan.easytrip.base_ui.theme.AppTheme
import ru.klekchyan.easytrip.navigation.BaseNav
import ru.klekchyan.easytrip.navigation.FavoritesNavigationGraph
import ru.klekchyan.easytrip.navigation.MainNavigationGraph

@Composable
fun AppBottomNavigation(
    navController: NavHostController,
) {
    BottomNavigation(
        backgroundColor = AppTheme.colors.secondaryBackground,
        elevation = 3.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        AppBottomNavigationItem(
            iconId = R.drawable.ic_map,
            rootNav = MainNavigationGraph.Root,
            firstRoute = MainNavigationGraph.MapScreen.route,
            currentDestination = currentDestination,
            navController = navController
        )
        AppBottomNavigationItem(
            iconId = R.drawable.ic_favorite,
            rootNav = FavoritesNavigationGraph.Root,
            firstRoute = FavoritesNavigationGraph.AllFavorites.route,
            currentDestination = currentDestination,
            navController = navController
        )
    }
}

@Composable
private fun RowScope.AppBottomNavigationItem(
    iconId: Int,
    rootNav: BaseNav,
    firstRoute: String,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == firstRoute } == true

    BottomNavigationItem(
        icon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null
            )
        },
        selectedContentColor = AppTheme.colors.primaryColor,
        unselectedContentColor = AppTheme.colors.secondaryColor,
        selected = selected,
        onClick = {
            if(!selected) navController.navigate(rootNav.url ?: rootNav.route)
        }
    )
}