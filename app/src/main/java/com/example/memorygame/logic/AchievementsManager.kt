package com.example.memorygame.logic

import androidx.compose.runtime.mutableStateListOf
import com.example.memorygame.model.Achievement

object AchievementsManager {
    val achievements = mutableStateListOf(
        Achievement("🎯 Primera partida", "Juega una partida completa"),
        Achievement("⏳ Contrarreloj", "Gana en modo contrarreloj"),
        Achievement("🔥 Desafío completado", "Termina una partida en modo desafío"),
        Achievement("👑 Perfecto", "Gana sin errores (pocos intentos extra)"),
        Achievement("Nivel Facil Completado", "Completa el nivel fácil una vez"),
        Achievement("Nivel Medio Completado", "Completa el nivel medio una vez"),
        Achievement("Nivel Difícil Completado", "Completa el nivel difícil una vez"),
        Achievement("Personalizado Completado", "Completa el nivel personalizado una vez en tamaño 10"),
        Achievement("Modo 2 Jugadores Completado", "Completa el nivel 2 jugadores una vez")

    )

    fun unlock(title: String) {
        achievements.find { it.title == title }?.unlocked = true
    }
}
