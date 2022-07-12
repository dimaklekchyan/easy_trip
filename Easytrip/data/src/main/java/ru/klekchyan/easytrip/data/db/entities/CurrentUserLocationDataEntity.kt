package ru.klekchyan.easytrip.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.klekchyan.easytrip.domain.entities.CurrentUserLocation

@Entity(tableName = "currentLocations")
data class CurrentUserLocationDataEntity(
    val longitude: Double,
    val latitude: Double,
    val isPrecise: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    fun toDomain() = CurrentUserLocation(longitude, latitude, isPrecise)
}
