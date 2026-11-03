package com.example.doggallery

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedDao {
    @Query("SELECT * FROM breeds WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun getBreeds(query: String): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE id = :id")
    fun getBreedById(id: Int): Flow<BreedEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreeds(breeds: List<BreedEntity>)

    @Query("UPDATE breeds SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean)
}