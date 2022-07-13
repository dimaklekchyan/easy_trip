package ru.klekchyan.easytrip.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.klekchyan.easytrip.data.db.daos.LocationDao
import ru.klekchyan.easytrip.data.db.entities.CurrentUserLocationDataEntity
import ru.klekchyan.easytrip.domain.entities.CurrentUserLocation
import ru.klekchyan.easytrip.domain.repositories.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao
): LocationRepository {

    override fun getCurrentLocationFlow(): Flow<CurrentUserLocation?> =
        locationDao.getCurrentLocationFlow().map { it?.toDomain() }


    override suspend fun saveLocation(longitude: Double, latitude: Double, isPrecise: Boolean) {
        locationDao.updateUserLocation(
            CurrentUserLocationDataEntity(
                longitude = longitude,
                latitude = latitude,
                isPrecise = isPrecise
            )
        )
    }
}