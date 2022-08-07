package ru.klekchyan.easytrip.navigation

sealed class MainNavigationGraph: BaseNav() {

    object Root: MainNavigationGraph() {
        override val route: String = "main"
        override val url: String? = null
    }

    object MapScreen: MainNavigationGraph() {
        override val route: String = "map_screen"
        override val url: String? = null
    }
}
