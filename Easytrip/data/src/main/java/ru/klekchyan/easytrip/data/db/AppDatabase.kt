package ru.klekchyan.easytrip.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.klekchyan.easytrip.data.db.daos.LocationDao
import ru.klekchyan.easytrip.data.db.entities.CurrentUserLocationDataEntity

@Database(
    entities = [
        CurrentUserLocationDataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
}