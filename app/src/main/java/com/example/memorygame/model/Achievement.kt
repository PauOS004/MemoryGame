package com.example.memorygame.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class Achievement(
    val title: String,
    val description: String,
) {
    var unlocked by mutableStateOf(false)
}



