package ru.klekchyan.easytrip.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.klekchyan.easytrip.data.db.daos.LocationDao
import ru.klekchyan.easytrip.data.db.daos.FavoritePlacesDao
import ru.klekchyan.easytrip.data.db.entities.CurrentUserLocationDataEntity
import ru.klekchyan.easytrip.data.db.entities.FavoritePlaceDataEntity

@Database(
    entities = [
        CurrentUserLocationDataEntity::class,
        FavoritePlaceDataEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun favoritePlacesDao(): FavoritePlacesDao
}