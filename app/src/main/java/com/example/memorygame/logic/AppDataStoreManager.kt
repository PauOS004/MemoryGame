package com.example.memorygame.logic

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import com.example.memorygame.util.Constants

val Context.appDataStore by preferencesDataStore(name = "memory_game_data")

class AppDataStoreManager(private val context: Context) {

    companion object {
        // User prefs
        val ALIAS = stringPreferencesKey(Constants.KEY_ALIAS)
        val VOLUME = floatPreferencesKey(Constants.KEY_VOLUME)
        val EMOJI_PACK = stringPreferencesKey(Constants.KEY_EMOJI_PACK)
        val BACKGROUND_PACK = stringPreferencesKey(Constants.KEY_BACKGROUND_PACK)
        val CARD_STYLE = stringPreferencesKey(Constants.KEY_CARD_STYLE)
        val MUSIC_TRACK = stringPreferencesKey(Constants.KEY_MUSIC_TRACK)

        // Monedas y desbloqueos
        val COINS = intPreferencesKey(Constants.KEY_COINS)
        val UNLOCKED_BACKGROUNDS = stringSetPreferencesKey(Constants.KEY_UNLOCKED_BACKGROUNDS)
        val UNLOCKED_MUSICS = stringSetPreferencesKey(Constants.KEY_UNLOCKED_MUSICS)
        val UNLOCKED_THEMES = stringSetPreferencesKey(Constants.KEY_UNLOCKED_THEMES)
        val UNLOCKED_CARD_STYLES = stringSetPreferencesKey(Constants.KEY_UNLOCKED_CARD_STYLES)

        // Logros
        val UNLOCKED_ACHIEVEMENTS = stringSetPreferencesKey(Constants.KEY_UNLOCKED_ACHIEVEMENTS)

    }

    // Cargar todo (Flow)
    val appDataFlow: Flow<AppData> = context.appDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            AppData(
                alias = prefs[ALIAS] ?: Constants.DEFAULT_ALIAS,
                volume = prefs[VOLUME] ?: Constants.DEFAULT_VOLUME,
                emojiPack = prefs[EMOJI_PACK] ?: Constants.DEFAULT_EMOJI_PACK,
                backgroundPack = prefs[BACKGROUND_PACK] ?: Constants.DEFAULT_BACKGROUND_PACK,
                cardStyle = prefs[CARD_STYLE] ?: Constants.DEFAULT_CARD_STYLE,
                musicTrack = prefs[MUSIC_TRACK] ?: Constants.DEFAULT_MUSIC_TRACK,
                coins = prefs[COINS] ?: 0,
                unlockedBackgrounds = prefs[UNLOCKED_BACKGROUNDS]
                    ?: setOf(Constants.DEFAULT_BACKGROUND_PACK),
                unlockedMusics = prefs[UNLOCKED_MUSICS] ?: setOf(Constants.DEFAULT_MUSIC_TRACK),
                unlockedThemes = prefs[UNLOCKED_THEMES] ?: setOf(Constants.DEFAULT_EMOJI_PACK),
                unlockedCardStyles = prefs[UNLOCKED_CARD_STYLES]
                    ?: setOf(Constants.DEFAULT_CARD_STYLE),
                unlockedAchievements = prefs[UNLOCKED_ACHIEVEMENTS] ?: emptySet()
            )
        }

    // Guardar todo
    suspend fun saveAppData(data: AppData) {
        try {
            context.appDataStore.edit { prefs ->
                prefs[ALIAS] = data.alias
                prefs[VOLUME] = data.volume
                prefs[EMOJI_PACK] = data.emojiPack
                prefs[BACKGROUND_PACK] = data.backgroundPack
                prefs[CARD_STYLE] = data.cardStyle
                prefs[MUSIC_TRACK] = data.musicTrack
                prefs[COINS] = data.coins
                prefs[UNLOCKED_BACKGROUNDS] = data.unlockedBackgrounds
                prefs[UNLOCKED_MUSICS] = data.unlockedMusics
                prefs[UNLOCKED_THEMES] = data.unlockedThemes
                prefs[UNLOCKED_CARD_STYLES] = data.unlockedCardStyles
                prefs[UNLOCKED_ACHIEVEMENTS] = data.unlockedAchievements
            }
        } catch (e: Exception) {
            Log.e("DataStore", "Error al guardar datos", e)
        }
    }
}


data class AppData(
    val alias: String,
    val volume: Float,
    val emojiPack: String,
    val backgroundPack: String,
    val cardStyle: String,
    val musicTrack: String,
    val coins: Int,
    val unlockedBackgrounds: Set<String>,
    val unlockedMusics: Set<String>,
    val unlockedThemes: Set<String>,
    val unlockedCardStyles: Set<String>,
    val unlockedAchievements: Set<String>,
)


