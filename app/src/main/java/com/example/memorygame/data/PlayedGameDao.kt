package com.example.memorygame.data

import androidx.room.*
import com.example.memorygame.model.PlayedGameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayedGameDao {
    @Insert
    suspend fun insert(game: PlayedGameEntity)

    @Query("SELECT * FROM played_games ORDER BY date DESC")
    fun getAllGames(): Flow<List<PlayedGameEntity>>

    @Query("SELECT * FROM played_games WHERE id = :id")
    suspend fun getGameById(id: Int): PlayedGameEntity?

    @Query("DELETE FROM played_games")
    suspend fun clearGames()
}
