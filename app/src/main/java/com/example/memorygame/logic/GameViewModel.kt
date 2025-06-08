package com.example.memorygame.logic

import android.app.Application
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.memorygame.R
import com.example.memorygame.data.AppDatabase
import com.example.memorygame.model.MemoryCard
import com.example.memorygame.repository.GameRepository
import com.example.memorygame.util.Constants.DEFAULT_GRID_SIZE
import com.example.memorygame.util.Constants.DEFAULT_PAR_NUM
import com.example.memorygame.util.Constants.MAX_TIMER_SECONDS
import com.example.memorygame.util.Constants.PERFECT_ATTEMPTS_MARGIN
import com.example.memorygame.model.PlayedGameEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepository by lazy {
        val db = AppDatabase.getDatabase(getApplication())
        GameRepository(AppDataStoreManager(getApplication()), db.playedGameDao())
    }
    private val _appData = MutableStateFlow<AppData?>(null)
    val appData: StateFlow<AppData?> = _appData
    val gameHistory: StateFlow<List<PlayedGameEntity>> = repository.getGameHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // -------------------
    // PREFERENCIAS Y ESTADO GENERAL
    // -------------------
    var alias = mutableStateOf("Jugador")
    var volume = mutableFloatStateOf(1f)
    var selectedThemeName = mutableStateOf("Frutas y Verduras")
    var selectedCardStyle = mutableStateOf("Clásico")
    var selectedBackgroundName = mutableStateOf("Clásico")
    var selectedMusicName = mutableStateOf("Pista 1")

    // -------------------
    // ESTADO DEL JUEGO
    // -------------------
    val cards = mutableStateListOf<MemoryCard>()
    private var selectedCard1 = mutableStateOf<MemoryCard?>(null)
    private var selectedCard2 = mutableStateOf<MemoryCard?>(null)
    private var isChecking = mutableStateOf(false)
    var hasWon = mutableStateOf(false)
    var attempts = mutableIntStateOf(0)
    var time = mutableIntStateOf(0)
    var timeExpired = mutableStateOf(false)
    val gameLog = mutableStateListOf<String>()
    var lastGameLog = listOf<String>()
    var selectedGame = mutableStateOf<PlayedGameEntity?>(null)


    // Monedas
    var coins = mutableIntStateOf(0)
    var coinsEarned = mutableIntStateOf(0)

    // Desbloqueos
    val unlockedBackgrounds = mutableStateListOf("Clásico")
    val unlockedMusics = mutableStateListOf("Pista 1")

    // Estado de la partida actual
    private var currentGridSize: Int = DEFAULT_GRID_SIZE
    private var currentAlias: String = ""
    private var currentUseTimer: Boolean = false
    private var currentIsChallengeMode: Boolean = false
    private var currentIsAchivementeChallenge: Boolean = false
    private var currentIsEasyMode: Boolean = false
    private var currentIsMediumMode: Boolean = false
    private var currentIsHardMode: Boolean = false
    private var currentIsPersMode: Boolean = false

    // Opciones disponibles
    val availableThemes = listOf(
        EmojiTheme("Frutas y Verduras", listOf("🍎", "🍌", "🍇", "🍓", "🍒", "🍍", "🍏", "🍐", "🍊", "🍉", "🍑", "🥥", "🥝", "🧀", "🍈", "🥭", "🥬", "🥑", "🍆", "🥔"), 0, true),
        EmojiTheme("Animales", listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐻‍❄️", "🐨", "🐯", "🦁", "🐮", "🐷", "🐽", "🐸", "🐵", "🐔", "🐧", "🐥"), 50),
        EmojiTheme("Emociones", listOf("😒", "😂", "😍", "😡", "😭", "😱", "😅", "😆", "🤩", "😎", "😤", "😢", "😳", "🤯", "😴", "😇", "😈", "🥰", "🙄", "😬"), 100),
        EmojiTheme("Comida", listOf("🍔", "🍟", "🍕", "🌮", "🍣", "🍩", "🥪", "🍝", "🍜", "🍰", "🍗", "🥓", "🍞", "🌯", "🍛", "🍪", "🍫", "🧀", "🥚", "🥗"), 150)
    )

    val cardStyles = listOf(
        CardStyle("Clásico", "❓", 0, true),
        CardStyle("Estrella", "⭐", 50),
        CardStyle("Corazón", "❤️", 100),
        CardStyle("Fuego", "🔥", 150)
    )

    val backgroundStyles = listOf(
        BackgroundStyle("Clásico", R.drawable.bg_ingame, 0, true),
        BackgroundStyle("Desierto", R.drawable.bg_desierto, 100),
        BackgroundStyle("Espacio", R.drawable.bg_space, 100),
        BackgroundStyle("Ciudad", R.drawable.bg_city, 100)
    )

    val musicOptions = listOf(
        MusicTrack("Pista 1", R.raw.mainmusic, 0),
        MusicTrack("Pista 2", R.raw.chill1, 50),
        MusicTrack("Pista 3", R.raw.chillgamer, 100),
        MusicTrack("Pista 4", R.raw.minecraft, 150)
    )

    private var timerJob = mutableStateOf<Job?>(null)


    init {
        viewModelScope.launch {
            repository.appDataFlow.collect { data ->
                _appData.value = data
                coins.intValue = 300
                unlockedBackgrounds.clear()
                unlockedBackgrounds.addAll(data.unlockedBackgrounds)
                unlockedMusics.clear()
                unlockedMusics.addAll(data.unlockedMusics)



                alias.value = data.alias
                volume.floatValue = data.volume
                selectedThemeName.value = data.emojiPack
                selectedCardStyle.value = data.cardStyle
                selectedBackgroundName.value = data.backgroundPack
                selectedMusicName.value = data.musicTrack
                AchievementsManager.setUnlocked(data.unlockedAchievements)
            }
        }
    }

    private fun resetGameState() {
        gameLog.clear()
        cards.clear()
        selectedCard1.value = null
        selectedCard2.value = null
        isChecking.value = false
        hasWon.value = false
        attempts.intValue = 0
        time.intValue = 0
        timeExpired.value = false
        timerJob.value?.cancel()
    }

    fun startGame(
        gridSize: Int,
        alias: String,
        useTimer: Boolean,
        isChallengeMode: Boolean,
        isAchivementeChallenge: Boolean,
        isEasyMode: Boolean,
        isMediumMode: Boolean,
        isHardMode: Boolean,
        isPersMode: Boolean
    ) {
        resetGameState()

        // Guardar estado actual
        currentGridSize = gridSize
        currentAlias = alias
        currentUseTimer = useTimer
        currentIsChallengeMode = isChallengeMode
        currentIsAchivementeChallenge = isAchivementeChallenge
        currentIsEasyMode = isEasyMode
        currentIsMediumMode = isMediumMode
        currentIsHardMode = isHardMode
        currentIsPersMode = isPersMode

        // Generar cartas
        cards.addAll(generateCardPairs(gridSize))

        // Iniciar timer
        if (useTimer) {
            incrementTimer()
        }
    }

    fun resetGame(gridSize: Int) {
        resetGameState()
        cards.addAll(generateCardPairs(gridSize))

        // Reiniciar el timer
        if (currentUseTimer) {
            incrementTimer()
        }
    }

    fun flipCard(card: MemoryCard) {
        if (!card.isFaceUp && !card.isMatched && !isChecking.value && !timeExpired.value) {
            card.isFaceUp = true
            if (selectedCard1.value == null) {
                selectedCard1.value = card
                gameLog.add("Levantar carta 1: ${card.content}")
            } else if (selectedCard2.value == null) {
                selectedCard2.value = card
                gameLog.add("Levantar carta 2: ${card.content}")
                checkCardsMatch()
            }
        }
    }

    private fun checkCardsMatch() {
        viewModelScope.launch {
            isChecking.value = true
            attempts.intValue++
            delay(1000)

            val c1 = selectedCard1.value?.content ?: ""
            val c2 = selectedCard2.value?.content ?: ""
            val turno = attempts.intValue

            if (c1 == c2) {
                gameLog.add("Turno $turno: $c1 + $c2 => Match")
            } else {
                gameLog.add("Turno $turno: $c1 + $c2 ≠ Match")
            }

            if (selectedCard1.value?.content == selectedCard2.value?.content) {
                selectedCard1.value?.isMatched = true
                selectedCard2.value?.isMatched = true

                if (cards.all { it.isMatched }) {
                    hasWon.value = true
                    handleWinLogic()
                }
            } else {
                selectedCard1.value?.isFaceUp = false
                selectedCard2.value?.isFaceUp = false
            }

            selectedCard1.value = null
            selectedCard2.value = null
            isChecking.value = false
        }
    }

    private fun incrementTimer() {
        timerJob.value?.cancel()
        time.intValue = 0
        timeExpired.value = false

        timerJob.value = viewModelScope.launch {
            while (!hasWon.value && !timeExpired.value) {
                delay(1000)
                time.intValue++

                // En modo contrarreloj verficar si se ha alcanzado el límite de 60 segundos
                if (currentIsChallengeMode && time.intValue >= MAX_TIMER_SECONDS) {
                    timeExpired.value = true
                    break
                }
            }
        }
    }

    private fun handleWinLogic() {
        var achievementsUnlocked = false

        if (!AchievementsManager.isUnlocked("🎯 Primera partida")) {
            AchievementsManager.unlock("🎯 Primera partida")
            achievementsUnlocked = true
        }
        if (currentIsChallengeMode && !AchievementsManager.isUnlocked("⏳ Contrarreloj")) {
            AchievementsManager.unlock("⏳ Contrarreloj")
            achievementsUnlocked = true
        }
        if (currentIsAchivementeChallenge && !AchievementsManager.isUnlocked("🔥 Desafío completado")) {
            AchievementsManager.unlock("🔥 Desafío completado")
            achievementsUnlocked = true
        }
        if (attempts.intValue <= (cards.size + PERFECT_ATTEMPTS_MARGIN) && !AchievementsManager.isUnlocked("👑 Perfecto")) {
            AchievementsManager.unlock("👑 Perfecto")
            achievementsUnlocked = true
        }
        if (currentIsEasyMode && !AchievementsManager.isUnlocked("Nivel Facil Completado")) {
            AchievementsManager.unlock("Nivel Facil Completado")
            achievementsUnlocked = true
        }
        if (currentIsMediumMode && !AchievementsManager.isUnlocked("Nivel Medio Completado")) {
            AchievementsManager.unlock("Nivel Medio Completado")
            achievementsUnlocked = true
        }
        if (currentIsHardMode && !AchievementsManager.isUnlocked("Nivel Difícil Completado")) {
            AchievementsManager.unlock("Nivel Difícil Completado")
            achievementsUnlocked = true
        }
        if (currentIsPersMode && cards.size == 40 && !AchievementsManager.isUnlocked("Personalizado Completado")) {
            AchievementsManager.unlock("Personalizado Completado")
            achievementsUnlocked = true
        }

        val earned = when {
            currentIsChallengeMode -> 20
            currentIsPersMode -> (cards.size / 4 - 1) * 5
            currentIsEasyMode -> 10
            currentIsMediumMode -> 15
            currentIsHardMode -> 20
            currentIsAchivementeChallenge -> 5
            else -> 0
        }

        coinsEarned.intValue = earned
        coins.value += earned

        if (achievementsUnlocked) {
            saveProgress()
        }

        timerJob.value?.cancel()
    }

    fun onGameFinished() {
        viewModelScope.launch {
            val logString = gameLog.joinToString("~")
            val game = PlayedGameEntity(
                alias = currentAlias,
                gridSize = currentGridSize,
                useTimer = currentUseTimer,
                time = time.intValue,
                attempts = attempts.intValue,
                hasWon = hasWon.value,
                date = System.currentTimeMillis(),
                mode = determineMode(),
                log = logString
            )
            repository.insertGame(game)
        }
        hasWon.value = false
        timeExpired.value = false
    }


    private fun generateCardPairs(gridSize: Int): List<MemoryCard> {
        val totalCards = gridSize * DEFAULT_GRID_SIZE
        val pairCount = totalCards / DEFAULT_PAR_NUM
        val symbols = getActiveTheme().emojis.shuffled().take(pairCount)
        val cardContents = (symbols + symbols).shuffled()
        return cardContents.mapIndexed { index, content ->
            MemoryCard(id = index, content = content)
        }
    }

    private fun getActiveTheme() = availableThemes.find { it.name == selectedThemeName.value } ?: availableThemes.first()
    fun getActiveCardStyle() = cardStyles.find { it.name == selectedCardStyle.value } ?: cardStyles.first()
    fun getActiveBackground() = backgroundStyles.find { it.name == selectedBackgroundName.value } ?: backgroundStyles.first()

    fun setSelectedTheme(name: String) { selectedThemeName.value = name }
    fun setSelectedCardStyle(name: String) { selectedCardStyle.value = name }
    fun setSelectedBackground(name: String) { selectedBackgroundName.value = name }
    fun setSelectedMusic(name: String) { selectedMusicName.value = name }
    fun unlockBackground(name: String) { if (!unlockedBackgrounds.contains(name)) unlockedBackgrounds.add(name) }
    fun unlockMusic(name: String) { if (!unlockedMusics.contains(name)) unlockedMusics.add(name) }

    // Funcion para verificar si el estado actual coincide con los parametros dados
    fun isStateMatching(
        gridSize: Int,
        alias: String,
        useTimer: Boolean,
        isChallengeMode: Boolean,
        isAchivementeChallenge: Boolean
    ): Boolean {
        return currentGridSize == gridSize &&
                currentAlias == alias &&
                currentUseTimer == useTimer &&
                currentIsChallengeMode == isChallengeMode &&
                currentIsAchivementeChallenge == isAchivementeChallenge
    }

    fun hasActiveGame(): Boolean {
        return cards.isNotEmpty()
    }


    private fun determineMode(): String {
        return when {
            currentIsChallengeMode -> "Contrarreloj"
            currentIsAchivementeChallenge -> "Desafío"
            currentIsEasyMode -> "Fácil"
            currentIsMediumMode -> "Medio"
            currentIsHardMode -> "Difícil"
            currentIsPersMode -> "Personalizado"
            else -> "Normal"
        }
    }

    fun saveProgress() {
        viewModelScope.launch {
            repository.saveAppData((
                AppData(
                    alias = alias.value,
                    volume = volume.floatValue,
                    emojiPack = selectedThemeName.value,
                    backgroundPack = selectedBackgroundName.value,
                    cardStyle = selectedCardStyle.value,
                    musicTrack = selectedMusicName.value,
                    coins = coins.intValue,
                    unlockedBackgrounds = unlockedBackgrounds.toSet(),
                    unlockedMusics = unlockedMusics.toSet(),
                    unlockedThemes = availableThemes.filter { it.unlocked }.map { it.name }.toSet(),
                    unlockedCardStyles = cardStyles.filter { it.unlocked }.map { it.name }.toSet(),
                    unlockedAchievements = AchievementsManager.unlockedTitles()
                )
            ))
        }
    }

    fun selectGame(game: PlayedGameEntity) {
        selectedGame.value = game
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearGameHistory()
        }
    }

    fun reloadAppData() {
        viewModelScope.launch {
            repository.appDataFlow.first().let { data ->
                _appData.value = data
                coins.intValue = data.coins
                unlockedBackgrounds.clear()
                unlockedBackgrounds.addAll(data.unlockedBackgrounds)
                unlockedMusics.clear()
                unlockedMusics.addAll(data.unlockedMusics)

                alias.value = data.alias
                volume.floatValue = data.volume
                selectedThemeName.value = data.emojiPack
                selectedCardStyle.value = data.cardStyle
                selectedBackgroundName.value = data.backgroundPack
                selectedMusicName.value = data.musicTrack
                AchievementsManager.setUnlocked(data.unlockedAchievements)
            }
        }
    }


}

// Clases de datos
data class EmojiTheme(val name: String, val emojis: List<String>, val price: Int, var unlocked: Boolean = false)
data class CardStyle(val name: String, val preview: String, val price: Int, var unlocked: Boolean = false)
data class BackgroundStyle(val name: String, val resourceId: Int, val price: Int, var unlocked: Boolean = false)
data class MusicTrack(val name: String, val resId: Int, val price: Int, var unlocked: Boolean = false)
