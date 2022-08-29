package ru.klekchyan.easytrip.domain.entities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import ru.klekchyan.easytrip.base_ui.theme.*

@Immutable
data class Catalog(
    val name: String,
    val num: String,
    val children: List<CatalogChild>
)

@Immutable
data class CatalogChild(
    val id: String,
    val name: String,
    val num: String,
    val children: List<CatalogChild>? = null
) {
    val backgroundColor: Color = when {
        num.startsWith("1") -> colorBlue
        num.startsWith("2") -> colorOrange
        num.startsWith("3") -> colorLightBlue
        num.startsWith("4") -> colorRed
        num.startsWith("5") -> colorLightDirtyGreen
        num.startsWith("6") -> colorPurple
        else -> colorBlue
    }
    val textOnBackgroundColor: Color =
        if(backgroundColor == colorLightDirtyGreen) Color.Black else Color.White
}
