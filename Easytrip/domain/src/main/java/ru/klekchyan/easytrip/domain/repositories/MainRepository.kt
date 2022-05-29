package ru.klekchyan.easytrip.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.klekchyan.easytrip.common.Either
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.entities.GeoName
import ru.klekchyan.easytrip.domain.entities.SimplePlace

interface MainRepository {

    fun getPlaceGeoNameFlow(name: String): Flow<Either<GeoName>>
    fun getSimplePlacesFlow(
        radius: Double,
        longitude: Double,
        latitude: Double
    ): Flow<Either<List<SimplePlace>>>
    fun getDetailedPlaceFlow(xid: String): Flow<Either<DetailedPlace>>
}