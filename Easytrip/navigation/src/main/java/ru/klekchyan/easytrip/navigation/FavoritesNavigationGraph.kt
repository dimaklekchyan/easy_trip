package ru.klekchyan.easytrip.navigation

sealed class FavoritesNavigationGraph: BaseNav() {

    object Root: FavoritesNavigationGraph() {
        override val route: String = "favorites"
        override val url: String? = null
    }

    object AllFavorites: FavoritesNavigationGraph() {
        override val route: String = "favorites_screen"
        override val url: String? = null
    }

    class SpecificFavorite(xid: String = ""): FavoritesNavigationGraph() {
        override val route: String = "favorites_screen/{xid}"
        override val url: String = "favorites_screen/${xid}"
    }
}