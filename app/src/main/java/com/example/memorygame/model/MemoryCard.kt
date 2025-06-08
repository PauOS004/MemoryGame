package com.example.memorygame.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class MemoryCard(
    val id: Int,
    val content: String,
    var isMatched: Boolean = false
) {
    var isFaceUp by mutableStateOf(false)
}