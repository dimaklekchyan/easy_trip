package ru.klekchyan.easytrip.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import ru.klekchyan.easytrip.data.api.services.BaseMapService
import ru.klekchyan.easytrip.data.api.services.CatalogMapService
import ru.klekchyan.easytrip.data.repositories.MainRepositoryImpl
import ru.klekchyan.easytrip.domain.repositories.MainRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {

    /*
        Network
    */
    @Provides
    @Singleton
    fun provideOkhttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient
            .Builder()
            .retryOnConnectionFailure(true)
            .addInterceptor(logging)
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    fun provideConverterFactory(): Converter.Factory {
        val contentType = "application/json".toMediaType()
        val format = Json { ignoreUnknownKeys = true }
        return format.asConverterFactory(contentType)
    }

    @Provides
    fun provideBaseMapService(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): BaseMapService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .baseUrl("https://api.opentripmap.com/0.1/")
            .build()
            .create(BaseMapService::class.java)
    }

    @Provides
    fun provideCatalogMapService(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): CatalogMapService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .baseUrl("https://opentripmap.io/")
            .build()
            .create(CatalogMapService::class.java)
    }

    /*
   Repositories
    */
    @Provides
    @Singleton
    fun bindMainRepository(r: MainRepositoryImpl): MainRepository = r
}