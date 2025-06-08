package com.example.memorygame.util

object Constants {
    // App DataStore Keys
    const val KEY_ALIAS = "alias"
    const val KEY_VOLUME = "music_volume"
    const val KEY_EMOJI_PACK = "emoji_pack"
    const val KEY_BACKGROUND_PACK = "background_pack"
    const val KEY_CARD_STYLE = "card_style"
    const val KEY_MUSIC_TRACK = "music_track"
    const val KEY_COINS = "coins"
    const val KEY_UNLOCKED_BACKGROUNDS = "unlocked_backgrounds"
    const val KEY_UNLOCKED_MUSICS = "unlocked_musics"
    const val KEY_UNLOCKED_THEMES = "unlocked_themes"
    const val KEY_UNLOCKED_CARD_STYLES = "unlocked_card_styles"
    const val KEY_UNLOCKED_ACHIEVEMENTS = "unlocked_achievements"

    // Default values
    const val DEFAULT_ALIAS = "Jugador"
    const val MAX_LENGTH_ALIAS = 10
    const val DEFAULT_VOLUME = 1f
    const val DEFAULT_EMOJI_PACK = "Frutas y Verduras"
    const val DEFAULT_BACKGROUND_PACK = "Clásico"
    const val DEFAULT_CARD_STYLE = "Clásico"
    const val DEFAULT_MUSIC_TRACK = "Pista 1"

    // Game logic
    const val MAX_TIMER_SECONDS = 60
    const val DEFAULT_GRID_SIZE = 4
    const val PERFECT_ATTEMPTS_MARGIN = 10
    const val DEFAULT_PAR_NUM = 2

    // Navigation route keys
    const val DEFAULT_GAME_ALIAS = "Player"

    // Achievement Titles
    const val ACHIEVEMENT_FIRST_GAME = "🎯 Primera partida"
    const val ACHIEVEMENT_CHALLENGE_MODE = "⏳ Contrarreloj"
    const val ACHIEVEMENT_ACHIEVEMENT_CHALLENGE = "🔥 Desafío completado"
    const val ACHIEVEMENT_PERFECT = "👑 Perfecto"
    const val ACHIEVEMENT_EASY = "Nivel Facil Completado"
    const val ACHIEVEMENT_MEDIUM = "Nivel Medio Completado"
    const val ACHIEVEMENT_HARD = "Nivel Difícil Completado"
    const val ACHIEVEMENT_CUSTOM = "Personalizado Completado"
}
