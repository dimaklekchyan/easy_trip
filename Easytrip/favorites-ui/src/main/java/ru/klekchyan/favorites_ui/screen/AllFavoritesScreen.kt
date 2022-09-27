package ru.klekchyan.favorites_ui.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.klekchyan.favorites_ui.vm.AllFavoritesScreenViewModel

@Composable
internal fun AllFavoritesScreen(
    vm: AllFavoritesScreenViewModel
) {

    val favorites = vm.favoritesPlaces

    LazyColumn(
        modifier = Modifier
    ) {
        items(
            items = favorites,
            key = { it.xid }
        ) { place ->
            Text(text = place.xid)
        }
    }
}