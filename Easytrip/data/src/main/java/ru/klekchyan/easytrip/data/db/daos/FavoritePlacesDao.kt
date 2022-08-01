package ru.klekchyan.easytrip.data.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.klekchyan.easytrip.data.db.entities.FavoritePlaceDataEntity

@Dao
interface FavoritePlacesDao {

    @Query("SELECT * FROM favorite_places")
    fun getAllFavoritePlacesFlow(): Flow<List<FavoritePlaceDataEntity>>

    @Query("SELECT * FROM favorite_places WHERE xid = :xid")
    fun getFavoritePlaceFlow(xid: String): Flow<FavoritePlaceDataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailedPlace(place: FavoritePlaceDataEntity)

    @Query("DELETE FROM favorite_places WHERE xid = :xid")
    suspend fun deletePlaceFromFavorite(xid: String)

}