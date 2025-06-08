package com.example.memorygame.logic

import Achievement
import androidx.compose.runtime.mutableStateListOf
import com.example.memorygame.util.Constants

object AchievementsManager {
    val achievements = mutableStateListOf(
        Achievement(Constants.ACHIEVEMENT_FIRST_GAME, "Juega una partida completa"),
        Achievement(Constants.ACHIEVEMENT_CHALLENGE_MODE, "Gana en modo contrarreloj"),
        Achievement(Constants.ACHIEVEMENT_ACHIEVEMENT_CHALLENGE, "Termina una partida en modo desafío"),
        Achievement(Constants.ACHIEVEMENT_PERFECT, "Gana sin errores (pocos intentos extra)"),
        Achievement(Constants.ACHIEVEMENT_EASY, "Completa el nivel fácil una vez"),
        Achievement(Constants.ACHIEVEMENT_MEDIUM, "Completa el nivel medio una vez"),
        Achievement(Constants.ACHIEVEMENT_HARD, "Completa el nivel difícil una vez"),
        Achievement(Constants.ACHIEVEMENT_CUSTOM, "Completa el nivel personalizado una vez en tamaño 10"),
    )

    fun unlock(title: String) {
        achievements.find { it.title == title }?.unlocked = true
    }

    fun isUnlocked(title: String): Boolean {
        return achievements.find { it.title == title }?.unlocked ?: false
    }

    fun unlockedTitles(): Set<String> = achievements.filter { it.unlocked }.map { it.title }.toSet()

    fun setUnlocked(unlockedSet: Set<String>) {
        achievements.forEach { it.unlocked = unlockedSet.contains(it.title) }
    }
}