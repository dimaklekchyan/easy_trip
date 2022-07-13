package ru.klekchyan.easytrip.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.klekchyan.easytrip.domain.entities.CurrentUserLocation

@Entity(tableName = "currentLocations")
data class CurrentUserLocationDataEntity(
    @PrimaryKey
    val id: Int = 1,
    val longitude: Double,
    val latitude: Double,
    val isPrecise: Boolean
) {
    fun toDomain() = CurrentUserLocation(longitude, latitude, isPrecise)
}
