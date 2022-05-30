package ru.klekchyan.easytrip.data.api

import ru.klekchyan.easytrip.common.AppError
import ru.klekchyan.easytrip.common.Either
import ru.klekchyan.easytrip.data.api.services.OpenTripMapService
import ru.klekchyan.easytrip.data.apiEntities.DetailedPlaceApiEntity
import ru.klekchyan.easytrip.data.apiEntities.GeoNameApiEntity
import ru.klekchyan.easytrip.data.apiEntities.SimplePlaceApiEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenTripMapApi @Inject constructor(
    private val openTripMapService: OpenTripMapService
) {
    suspend fun getPlacesGeoName(name: String): Either<GeoNameApiEntity> {
        return try {
            val result = openTripMapService.getPlacesGeoName(name = name)

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
        kinds: String?
    ): Either<List<SimplePlaceApiEntity>> {
        return try {
            val result = openTripMapService.getPlacesByRadius(
                radius = radius,
                longitude = longitude,
                latitude = latitude,
                kinds = kinds
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
        kinds: String?
    ): Either<List<SimplePlaceApiEntity>> {
        return try {
            val result = openTripMapService.getPlacesByRadiusAndName(
                name = name,
                radius = radius,
                longitude = longitude,
                latitude = latitude,
                kinds = kinds
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

    suspend fun getDetailedPlace(xid: String): Either<DetailedPlaceApiEntity> {
        return try {
            val result = openTripMapService.getDetailedPlace(xid = xid)

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