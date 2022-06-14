package ru.klekchyan.easytrip.data.api

import ru.klekchyan.easytrip.common.AppError
import ru.klekchyan.easytrip.common.Either
import ru.klekchyan.easytrip.data.api.services.BaseMapService
import ru.klekchyan.easytrip.data.api.services.CatalogMapService
import ru.klekchyan.easytrip.data.apiEntities.CatalogApiEntity
import ru.klekchyan.easytrip.data.apiEntities.DetailedPlaceApiEntity
import ru.klekchyan.easytrip.data.apiEntities.GeoNameApiEntity
import ru.klekchyan.easytrip.data.apiEntities.SimplePlaceApiEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapApi @Inject constructor(
    private val baseMapService: BaseMapService,
    private val catalogMapService: CatalogMapService
) {

    suspend fun getCatalog(language: String): Either<CatalogApiEntity> {
        return try {
            val result = if(language == "ru") {
                catalogMapService.getRussianCatalog()
            } else {
                catalogMapService.getEnglishCatalog()
            }

            if (result.isSuccessful) {
                val catalog = result.body() as CatalogApiEntity
                Either.success(catalog)
            } else {
                Either.error(AppError.Unknown().code, AppError.Unknown().message())
            }
        } catch (e: Exception) {
            Either.error(AppError.Unknown().code, AppError.Unknown().message())
        }
    }

    suspend fun getPlacesGeoName(name: String, language: String): Either<GeoNameApiEntity> {
        return try {
            val result = baseMapService.getPlacesGeoName(
                name = name,
                lang = if(language == "ru") "ru" else "en"
            )

            if (result.isSuccessful) {
                val geoName = result.body() as GeoNameApiEntity
                Either.success(geoName)
            } else {
                Either.error(AppError.Unknown().code, AppError.Unknown().message())
            }
        } catch (e: Exception) {
            Either.error(AppError.Unknown().code, AppError.Unknown().message())
        }
    }

    suspend fun getPlacesByRadius(
        radius: Double,
        longitude: Double,
        latitude: Double,
        kinds: String?,
        language: String
    ): Either<List<SimplePlaceApiEntity>> {
        return try {
            val result = baseMapService.getPlacesByRadius(
                radius = radius,
                longitude = longitude,
                latitude = latitude,
                kinds = kinds,
                lang = if(language == "ru") "ru" else "en"
            )

            if (result.isSuccessful) {
                val places = result.body() as List<SimplePlaceApiEntity>
                Either.success(places)
            } else {
                Either.error(AppError.Unknown().code, AppError.Unknown().message())
            }
        } catch (e: Exception) {
            Either.error(AppError.Unknown().code, AppError.Unknown().message())
        }
    }

    suspend fun getPlacesByRadiusAndName(
        name: String,
        radius: Double,
        longitude: Double,
        latitude: Double,
        kinds: String?,
        language: String
    ): Either<List<SimplePlaceApiEntity>> {
        return try {
            val result = baseMapService.getPlacesByRadiusAndName(
                name = name,
                radius = radius,
                longitude = longitude,
                latitude = latitude,
                kinds = kinds,
                lang = if(language == "ru") "ru" else "en"
            )

            if (result.isSuccessful) {
                val places = result.body() as List<SimplePlaceApiEntity>
                Either.success(places)
            } else {
                Either.error(AppError.Unknown().code, AppError.Unknown().message())
            }
        } catch (e: Exception) {
            Either.error(AppError.Unknown().code, AppError.Unknown().message())
        }
    }

    suspend fun getDetailedPlace(xid: String, language: String): Either<DetailedPlaceApiEntity> {
        return try {
            val result = baseMapService.getDetailedPlace(
                xid = xid,
                lang = if(language == "ru") "ru" else "en"
            )

            if (result.isSuccessful) {
                val place = result.body() as DetailedPlaceApiEntity
                Either.success(place)
            } else {
                Either.error(AppError.Unknown().code, AppError.Unknown().message())
            }
        } catch (e: Exception) {
            Either.error(AppError.Unknown().code, AppError.Unknown().message())
        }
    }
}