package ru.klekchyan.easytrip.data.api.services

import retrofit2.Response
import retrofit2.http.GET
import ru.klekchyan.easytrip.data.api.entities.CatalogApiEntity

interface CatalogMapService {

    @GET("catalog.ru.json")
    suspend fun getRussianCatalog(): Response<CatalogApiEntity>

    @GET("catalog.en.json")
    suspend fun getEnglishCatalog(): Response<CatalogApiEntity>
}