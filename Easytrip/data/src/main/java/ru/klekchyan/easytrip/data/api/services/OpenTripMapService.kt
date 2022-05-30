package ru.klekchyan.easytrip.data.api.services

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.klekchyan.easytrip.data.apiEntities.DetailedPlaceApiEntity
import ru.klekchyan.easytrip.data.apiEntities.GeoNameApiEntity
import ru.klekchyan.easytrip.data.apiEntities.SimplePlaceApiEntity

interface OpenTripMapService {

    @GET("{lang}/places/geoname")
    suspend fun getPlacesGeoName(
        @Path("lang") lang: String = "ru",
        @Query("name") name: String,
        @Query("apikey") apiKey: String = "5ae2e3f221c38a28845f05b6d1bf801e42751422b251f4b217147617"
    ): Response<GeoNameApiEntity>

    @GET("{lang}/places/autosuggest")
    suspend fun getPlacesByRadiusAndName(
        @Path("lang") lang: String = "ru",
        @Query("name") name: String,
        @Query("radius") radius: Double,
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double,
        @Query("kinds") kinds: String? = null,
        @Query("format") format: String = "json",
        @Query("apikey") apiKey: String = "5ae2e3f221c38a28845f05b6d1bf801e42751422b251f4b217147617"
    ): Response<List<SimplePlaceApiEntity>>

    @GET("{lang}/places/radius")
    suspend fun getPlacesByRadius(
        @Path("lang") lang: String = "ru",
        @Query("radius") radius: Double,
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double,
        @Query("kinds") kinds: String? = null,
        @Query("format") format: String = "json",
        @Query("apikey") apiKey: String = "5ae2e3f221c38a28845f05b6d1bf801e42751422b251f4b217147617"
    ): Response<List<SimplePlaceApiEntity>>

    @GET("{lang}/places/xid/{xid}")
    suspend fun getDetailedPlace(
        @Path("lang") lang: String = "ru",
        @Path("xid") xid: String,
        @Query("format") format: String = "json",
        @Query("apikey") apiKey: String = "5ae2e3f221c38a28845f05b6d1bf801e42751422b251f4b217147617"
    ): Response<DetailedPlaceApiEntity>
}