package ru.klekchyan.easytrip.domain.entities

data class GeoName(
    val name: String,
    val country: String,
    val longitude: Double,
    val latitude: Double,
    val timezone: String? = null,
    val population: Int? = null,
)
