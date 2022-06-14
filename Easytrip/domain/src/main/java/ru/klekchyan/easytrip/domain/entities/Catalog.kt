package ru.klekchyan.easytrip.domain.entities

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
)
