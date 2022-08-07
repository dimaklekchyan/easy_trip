package ru.klekchyan.easytrip

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.klekchyan.easytrip.base_ui.theme.AppTheme
import ru.klekchyan.easytrip.main_ui.addMain
import ru.klekchyan.easytrip.navigation.BaseNav
import ru.klekchyan.easytrip.navigation.FavoritesNavigationGraph
import ru.klekchyan.easytrip.navigation.MainNavigationGraph
import ru.klekchyan.favorites_ui.addFavorites

@Composable
fun ApplicationScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            if(true) {
                AppBottomNavigation(navController = navController)
            }
        }
    ) {
        NavHost(navController = navController, startDestination = MainNavigationGraph.Root.route) {
            addMain(navController)
            addFavorites(navController)
        }
    }
}

@Composable
private fun AppBottomNavigation(
    navController: NavHostController,
) {
    BottomNavigation(
        backgroundColor = AppTheme.colors.primaryBackground
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        AppBottomNavigationItem(
            label = "Карта",
            iconId = 0,
            rootNav = MainNavigationGraph.Root,
            firstRoute = MainNavigationGraph.MapScreen.route,
            currentDestination = currentDestination,
            navController = navController
        )
        AppBottomNavigationItem(
            label = "Избранное",
            iconId = 0,
            rootNav = FavoritesNavigationGraph.Root,
            firstRoute = FavoritesNavigationGraph.AllFavorites.route,
            currentDestination = currentDestination,
            navController = navController
        )
    }
}

@Composable
fun RowScope.AppBottomNavigationItem(
    label: String,
    iconId: Int,
    rootNav: BaseNav,
    firstRoute: String,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == firstRoute } == true

    BottomNavigationItem(
        icon = {
//            Icon(
//                painter = painterResource(id = iconId),
//                contentDescription = null
//            )
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null
            )
        },
        label = {
            Text(text = label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        selectedContentColor = Color.Red,
        unselectedContentColor = Color.Green,
        selected = selected,
        onClick = {
            if(!selected) navController.navigate(rootNav.url ?: rootNav.route)
        }
    )
}