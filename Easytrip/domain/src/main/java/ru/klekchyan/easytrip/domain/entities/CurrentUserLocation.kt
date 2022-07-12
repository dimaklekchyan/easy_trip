package ru.klekchyan.easytrip.domain.entities

data class CurrentUserLocation(
    val longitude: Double,
    val latitude: Double,
    val isPrecise: Boolean
)