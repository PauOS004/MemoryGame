package com.example.memorygame.logic

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memorygame.R
import com.example.memorygame.model.MemoryCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    // Estado del juego
    val cards = mutableStateListOf<MemoryCard>()
    var selectedCard1 = mutableStateOf<MemoryCard?>(null)
    var selectedCard2 = mutableStateOf<MemoryCard?>(null)
    var isChecking = mutableStateOf(false)
    var hasWon = mutableStateOf(false)
    var attempts = mutableStateOf(0)
    var time = mutableStateOf(0)
    var timeExpired = mutableStateOf(false)

    // Modo local
    var isLocalMode = false
    var isLocalGameStarted = mutableStateOf(false)
    var currentPlayer = mutableStateOf(1)
    var player1Score = mutableStateOf(0)
    var player2Score = mutableStateOf(0)

    // Monedas
    var coins = mutableStateOf(0)
    var coinsEarned = mutableStateOf(0)

    // Configuración seleccionada
    var selectedThemeName = mutableStateOf("Frutas y Verduras")
    var selectedCardStyle = mutableStateOf("Clásico")
    var selectedBackgroundName = mutableStateOf("Clásico")
    var selectedMusicName = mutableStateOf("MainMusic")

    // Desbloqueos
    val unlockedBackgrounds = mutableStateListOf("Clásico")
    val unlockedMusics = mutableStateListOf("MainMusic")

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

    fun startLocalGame(pairCount: Int) {
        isLocalMode = true
        isLocalGameStarted.value = true
        hasWon.value = false
        currentPlayer.value = 1
        player1Score.value = 0
        player2Score.value = 0

        cards.clear()
        cards.addAll(generateLocalCardPairs(pairCount))
        selectedCard1.value = null
        selectedCard2.value = null
        isChecking.value = false
    }

    fun flipCardLocal(card: MemoryCard) {
        if (!card.isFaceUp && !card.isMatched && !isChecking.value) {
            card.isFaceUp = true
            if (selectedCard1.value == null) {
                selectedCard1.value = card
            } else if (selectedCard2.value == null) {
                selectedCard2.value = card
                checkLocalMatch()
            }
        }
    }

    private fun checkLocalMatch() {
        viewModelScope.launch {
            isChecking.value = true
            delay(1000)

            if (selectedCard1.value?.content == selectedCard2.value?.content) {
                selectedCard1.value?.isMatched = true
                selectedCard2.value?.isMatched = true
                if (currentPlayer.value == 1) player1Score.value++ else player2Score.value++
                if (cards.all { it.isMatched }) {
                    hasWon.value = true
                    if (isLocalMode) {
                        AchievementsManager.unlock("Modo 2 Jugadores Completado")
                        coinsEarned.value = 5
                        coins.value += 5
                    }
                }
            } else {
                selectedCard1.value?.isFaceUp = false
                selectedCard2.value?.isFaceUp = false
                currentPlayer.value = if (currentPlayer.value == 1) 2 else 1
            }

            selectedCard1.value = null
            selectedCard2.value = null
            isChecking.value = false
        }
    }

    private var alias: String = ""
    private var useTimer: Boolean = false
    private var isChallengeMode: Boolean = false
    private var isAchivementeChallenge: Boolean = false
    private var isEasyMode: Boolean = false
    private var isMediumMode: Boolean = false
    private var isHardMode: Boolean = false
    private var isPersMode: Boolean = false

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
        this.alias = alias
        this.useTimer = useTimer
        this.isChallengeMode = isChallengeMode
        this.isAchivementeChallenge = isAchivementeChallenge
        this.isEasyMode = isEasyMode
        this.isMediumMode = isMediumMode
        this.isHardMode = isHardMode
        this.isPersMode = isPersMode

        resetGame(gridSize)
        incrementTimer()
    }


    fun resetGame(gridSize: Int) {
        val newCards = generateCardPairs(gridSize)
        cards.clear()
        cards.addAll(newCards)
        selectedCard1.value = null
        selectedCard2.value = null
        isChecking.value = false
        hasWon.value = false
        attempts.value = 0
        time.value = 0
        timeExpired.value = false
    }

    fun flipCard(card: MemoryCard) {
        if (!card.isFaceUp && !card.isMatched && !isChecking.value && !timeExpired.value) {
            card.isFaceUp = true
            if (selectedCard1.value == null) {
                selectedCard1.value = card
            } else if (selectedCard2.value == null) {
                selectedCard2.value = card
                checkCardsMatch()
            }
        }
    }

    private fun checkCardsMatch() {
        viewModelScope.launch {
            isChecking.value = true
            attempts.value++
            delay(1000)

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
        if (!useTimer) return
        timerJob.value?.cancel()

        timerJob.value = viewModelScope.launch {
            while (!hasWon.value && !timeExpired.value) {
                delay(1000)
                time.value++
                if (isChallengeMode && time.value >= 60) {
                    timeExpired.value = true
                }
            }
        }
    }

    private fun handleWinLogic() {
        AchievementsManager.unlock("🎯 Primera partida")
        if (isChallengeMode) AchievementsManager.unlock("⏳ Contrarreloj")
        if (isAchivementeChallenge) AchievementsManager.unlock("🔥 Desafío completado")
        if (attempts.value <= (cards.size + 10)) AchievementsManager.unlock("👑 Perfecto")
        if (isEasyMode) AchievementsManager.unlock("Nivel Facil Completado")
        if (isMediumMode) AchievementsManager.unlock("Nivel Medio Completado")
        if (isHardMode) AchievementsManager.unlock("Nivel Difícil Completado")
        if (isPersMode && cards.size == 40) AchievementsManager.unlock("Personalizado Completado")

        val earned = when {
            isChallengeMode -> 20
            isPersMode -> (cards.size / 4 - 1) * 5
            isEasyMode -> 10
            isMediumMode -> 15
            isHardMode -> 20
            isAchivementeChallenge -> 5
            else -> 0
        }

        coinsEarned.value = earned
        coins.value += earned
    }

    private fun generateCardPairs(gridSize: Int): List<MemoryCard> {
        var totalCards = gridSize * 4
        if (totalCards % 2 != 0) totalCards++
        val pairCount = totalCards / 2
        val symbols = getActiveTheme().emojis.shuffled().take(pairCount)
        val cardContents = (symbols + symbols).shuffled()
        return cardContents.mapIndexed { index, content ->
            MemoryCard(id = index, content = content)
        }
    }

    private fun generateLocalCardPairs(pairCount: Int): List<MemoryCard> {
        val symbols = getActiveTheme().emojis.shuffled().take(pairCount)
        val cardContents = (symbols + symbols).shuffled()
        return cardContents.mapIndexed { index, content ->
            MemoryCard(id = index, content = content)
        }
    }

    fun getActiveTheme() = availableThemes.find { it.name == selectedThemeName.value } ?: availableThemes.first()
    fun getActiveCardStyle() = cardStyles.find { it.name == selectedCardStyle.value } ?: cardStyles.first()
    fun getActiveBackground() = backgroundStyles.find { it.name == selectedBackgroundName.value } ?: backgroundStyles.first()
    fun getActiveMusic() = musicOptions.find { it.name == selectedMusicName.value } ?: musicOptions.first()

    fun setSelectedTheme(name: String) { selectedThemeName.value = name }
    fun setSelectedCardStyle(name: String) { selectedCardStyle.value = name }
    fun setSelectedBackground(name: String) { selectedBackgroundName.value = name }
    fun setSelectedMusic(name: String) { selectedMusicName.value = name }
    fun unlockBackground(name: String) { if (!unlockedBackgrounds.contains(name)) unlockedBackgrounds.add(name) }
    fun unlockMusic(name: String) { if (!unlockedMusics.contains(name)) unlockedMusics.add(name) }
}

// Clases de datos
data class EmojiTheme(val name: String, val emojis: List<String>, val price: Int, var unlocked: Boolean = false)
data class CardStyle(val name: String, val preview: String, val price: Int, var unlocked: Boolean = false)
data class BackgroundStyle(val name: String, val resourceId: Int, val price: Int, var unlocked: Boolean = false)
data class MusicTrack(val name: String, val resId: Int, val price: Int, var unlocked: Boolean = false)
