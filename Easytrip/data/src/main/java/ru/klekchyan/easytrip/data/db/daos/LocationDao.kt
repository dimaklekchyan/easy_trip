package ru.klekchyan.easytrip.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.klekchyan.easytrip.data.db.entities.CurrentUserLocationDataEntity

@Dao
interface LocationDao {
    @Query("SELECT * FROM currentLocations LIMIT 1")
    fun getCurrentLocationFlow(): Flow<CurrentUserLocationDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserLocation(location: CurrentUserLocationDataEntity)
}