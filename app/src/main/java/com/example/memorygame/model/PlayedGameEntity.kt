package com.example.memorygame.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "played_games")
class PlayedGameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val alias: String,
    val gridSize: Int,
    val useTimer: Boolean,
    val time: Int,
    val attempts: Int,
    val hasWon: Boolean,
    val date: Long,
    val mode: String,
    val log: String
)
