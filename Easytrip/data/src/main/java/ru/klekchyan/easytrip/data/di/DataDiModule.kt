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
import retrofit2.Retrofit
import ru.klekchyan.easytrip.data.api.services.OpenTripMapService
import ru.klekchyan.easytrip.data.repositories.MainRepositoryImpl
import ru.klekchyan.easytrip.domain.repositories.MainRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {


    //API
    @Provides
    @Singleton
    @ExperimentalSerializationApi
    fun provideRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        val converterFactory = Json.asConverterFactory(contentType)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient
            .Builder()
            .retryOnConnectionFailure(true)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .baseUrl("https://api.opentripmap.com/0.1/")
            .build()
    }

    @Provides
    fun provideOpenTripMapService(retrofit: Retrofit): OpenTripMapService =
        retrofit.create(OpenTripMapService::class.java)

    /*
   Repositories
    */
    @Provides
    @Singleton
    fun bindMainRepository(r: MainRepositoryImpl): MainRepository = r
}