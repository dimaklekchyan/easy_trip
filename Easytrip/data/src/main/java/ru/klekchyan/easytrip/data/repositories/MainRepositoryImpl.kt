package ru.klekchyan.easytrip.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.klekchyan.easytrip.common.Either
import ru.klekchyan.easytrip.data.api.OpenTripMapApi
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.entities.GeoName
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.domain.repositories.MainRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val openTripMapApi: OpenTripMapApi
): MainRepository {

    override fun getPlaceGeoNameFlow(name: String): Flow<Either<GeoName>> = flow {
        emit(Either.loading())

        val result = openTripMapApi.getPlacesGeoName(name)

        result.onError { code, info ->
            emit(Either.error(code, info))
        }

        result.onSuccess { geoNameApiEntity ->
            emit(Either.success(geoNameApiEntity?.toDomain()))
        }
    }

    override fun getSimplePlacesFlow(
        radius: Double,
        longitude: Double,
        latitude: Double
    ): Flow<Either<List<SimplePlace>>> = flow {
        emit(Either.loading())

        val result = openTripMapApi.getPlacesByRadius(radius, longitude, latitude)

        result.onError { code, info ->
            emit(Either.error(code, info))
        }

        result.onSuccess { simplePlaces ->
            emit(Either.success(simplePlaces?.map { it.toDomain() } ?: listOf()))
        }
    }

    override fun getDetailedPlaceFlow(xid: String): Flow<Either<DetailedPlace>> = flow {
        emit(Either.loading())

        val result = openTripMapApi.getDetailedPlace(xid)

        result.onError { code, info ->
            emit(Either.error(code, info))
        }

        result.onSuccess { detailedPlace ->
            emit(Either.success(detailedPlace!!.toDomain()))
        }
    }
}