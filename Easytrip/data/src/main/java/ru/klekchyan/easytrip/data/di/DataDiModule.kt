package ru.klekchyan.easytrip.data.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
import ru.klekchyan.easytrip.data.db.AppDatabase
import ru.klekchyan.easytrip.data.db.daos.LocationDao
import ru.klekchyan.easytrip.data.db.daos.FavoritePlacesDao
import ru.klekchyan.easytrip.data.repositories.LocationRepositoryImpl
import ru.klekchyan.easytrip.data.repositories.PlacesRepositoryImpl
import ru.klekchyan.easytrip.domain.repositories.LocationRepository
import ru.klekchyan.easytrip.domain.repositories.PlacesRepository
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
    DB
    */
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "appdb")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideLocationDao(db: AppDatabase): LocationDao = db.locationDao()

    @Provides
    @Singleton
    fun providePlaceDao(db: AppDatabase): FavoritePlacesDao = db.favoritePlacesDao()

    /*
   Repositories
    */
    @Provides
    @Singleton
    fun bindPlacesRepository(r: PlacesRepositoryImpl): PlacesRepository = r

    @Provides
    @Singleton
    fun bindLocationRepository(r: LocationRepositoryImpl): LocationRepository = r
}