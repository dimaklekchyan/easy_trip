package ru.klekchyan.easytrip.data.repositories

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.klekchyan.easytrip.common.Either
import ru.klekchyan.easytrip.data.api.MapApi
import ru.klekchyan.easytrip.domain.entities.Catalog
import ru.klekchyan.easytrip.domain.entities.DetailedPlace
import ru.klekchyan.easytrip.domain.entities.GeoName
import ru.klekchyan.easytrip.domain.entities.SimplePlace
import ru.klekchyan.easytrip.domain.repositories.MainRepository
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepositoryImpl @Inject constructor(
    private val mapApi: MapApi
): MainRepository {

    override fun getCatalogFlow(): Flow<Either<Catalog>> = flow {
        emit(Either.loading())

        val result = mapApi.getCatalog(getLocaleLanguage())

        result.onError { code, info ->
            emit(Either.error(code, info))
        }

        result.onSuccess { catalogApiEntity ->
            emit(Either.success(catalogApiEntity?.toDomain()))
        }
    }

    override fun getPlaceGeoNameFlow(name: String): Flow<Either<GeoName>> = flow {
        emit(Either.loading())

        val result = mapApi.getPlacesGeoName(
            name = name,
            language = getLocaleLanguage()
        )

        result.onError { code, info ->
            emit(Either.error(code, info))
        }

        result.onSuccess { geoNameApiEntity ->
            emit(Either.success(geoNameApiEntity?.toDomain()))
        }
    }

    override fun getPlacesByRadiusFlow(
        radius: Double,
        longitude: Double,
        latitude: Double,
        kinds: String?
    ): Flow<Either<List<SimplePlace>>> = flow {
        emit(Either.loading())

        val result = mapApi.getPlacesByRadius(
            radius = radius,
            longitude = longitude,
            latitude = latitude,
            kinds = kinds,
            language = getLocaleLanguage()
        )

        result.onError { code, info ->
            emit(Either.error(code, info))
        }

        result.onSuccess { simplePlaces ->
            emit(Either.success(simplePlaces?.map { it.toDomain() } ?: listOf()))
        }
    }

    override fun getPlacesByRadiusAndNameFlow(
        name: String,
        radius: Double,
        longitude: Double,
        latitude: Double,
        kinds: String?
    ): Flow<Either<List<SimplePlace>>> = flow {
        emit(Either.loading())

        val result = mapApi.getPlacesByRadiusAndName(
            name = name,
            radius = radius,
            longitude = longitude,
            latitude = latitude,
            kinds = kinds,
            language = getLocaleLanguage()
        )

        result.onError { code, info ->
            emit(Either.error(code, info))
        }

        result.onSuccess { simplePlaces ->
            emit(Either.success(simplePlaces?.map { it.toDomain() } ?: listOf()))
        }
    }

    override fun getDetailedPlaceFlow(xid: String): Flow<Either<DetailedPlace>> = flow {
        emit(Either.loading())

        val result = mapApi.getDetailedPlace(
            xid = xid,
            language = getLocaleLanguage()
        )

        result.onError { code, info ->
            emit(Either.error(code, info))
        }

        result.onSuccess { detailedPlace ->
            emit(Either.success(detailedPlace!!.toDomain()))
        }
    }

    private fun getLocaleLanguage() = Locale.getDefault().language.toString()
}