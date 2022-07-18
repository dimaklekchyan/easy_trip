package ru.klekchyan.easytrip.domain.entities

import androidx.compose.ui.graphics.Color

data class Catalog(
    val name: String,
    val num: String,
    val children: List<CatalogChild>
)

data class CatalogChild(
    val id: String,
    val name: String,
    val num: String,
    val children: List<CatalogChild>? = null
) {
    val color: Color = when {
        num.startsWith("1") -> Color(0xFF5FB461)
        num.startsWith("2") -> Color(0xFFF18B3D)
        num.startsWith("3") -> Color(0xFF64C5F1)
        num.startsWith("4") -> Color(0xFFE03462)
        num.startsWith("5") -> Color(0xFFAFCB42)
        num.startsWith("6") -> Color(0xFF353D8D)
        else -> Color(0xFFDADADA)
    }
}
