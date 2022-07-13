package ru.klekchyan.easytrip.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.klekchyan.easytrip.domain.entities.CurrentUserLocation

interface LocationRepository {
    suspend fun saveLocation(
        longitude: Double,
        latitude: Double,
        isPrecise: Boolean
    )
    fun getCurrentLocationFlow(): Flow<CurrentUserLocation?>
}