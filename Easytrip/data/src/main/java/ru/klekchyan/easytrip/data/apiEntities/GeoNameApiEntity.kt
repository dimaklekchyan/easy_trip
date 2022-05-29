package ru.klekchyan.easytrip.data.apiEntities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.klekchyan.easytrip.domain.entities.GeoName

@Serializable
data class GeoNameApiEntity(
    val name: String,
    val country: String,
    @SerialName("lat")
    val latitude: Double,
    @SerialName("lon")
    val longitude: Double,
    val timezone: String? = null,
    val population: Int? = null,
    val status: String? = null,
    @SerialName("partial_match")
    val partialMatch: Boolean = false
) {
    fun toDomain() = GeoName(name, country, longitude, latitude, timezone, population)
}