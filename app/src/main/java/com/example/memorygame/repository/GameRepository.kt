package com.example.memorygame.repository

import com.example.memorygame.logic.AppData
import com.example.memorygame.logic.AppDataStoreManager
import com.example.memorygame.logic.GameViewModel
import kotlinx.coroutines.flow.Flow
import com.example.memorygame.data.*
import com.example.memorygame.data.AppDatabase
import com.example.memorygame.model.PlayedGameEntity


class GameRepository(
    private val dataStoreManager: AppDataStoreManager,
    private val gameDao: PlayedGameDao
) {
    val appDataFlow = dataStoreManager.appDataFlow

    suspend fun saveAppData(data: AppData) {
        dataStoreManager.saveAppData(data)
    }

    fun getGameHistory(): Flow<List<PlayedGameEntity>> = gameDao.getAllGames()

    suspend fun insertGame(game: PlayedGameEntity) = gameDao.insert(game)

    suspend fun clearGameHistory() = gameDao.clearGames()
}

