package com.example.memorygame.screens

import android.media.MediaPlayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.memorygame.R
import com.example.memorygame.logic.GameViewModel
import com.example.memorygame.model.MemoryCard
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    navController: NavController,
    alias: String,
    gridSize: Int,
    useTimer: Boolean,
    isChallengeMode: Boolean = false,
    isAchivementeChallenge: Boolean = false,
    isEasyMode: Boolean = false,
    isMediumMode: Boolean = false,
    isHardMode: Boolean = false,
    isPersMode: Boolean = false,
    viewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current

    val cards = viewModel.cards
    val hasWon by viewModel.hasWon
    val attempts by viewModel.attempts
    val time by viewModel.time
    val timeExpired by viewModel.timeExpired

    val currentCardStyle = viewModel.getActiveCardStyle()
    val currentBackground = viewModel.getActiveBackground()

    var musicPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(true) }
    val selectedMusic = viewModel.selectedMusicName.value
    val selectedTrack = viewModel.musicOptions.find { it.name == selectedMusic }

    LaunchedEffect(Unit) {
            viewModel.startGame(
                gridSize = gridSize,
                alias = alias,
                useTimer = useTimer,
                isChallengeMode = isChallengeMode,
                isAchivementeChallenge = isAchivementeChallenge,
                isEasyMode = isEasyMode,
                isMediumMode = isMediumMode,
                isHardMode = isHardMode,
                isPersMode = isPersMode
            )
        selectedTrack?.let {
            musicPlayer = MediaPlayer.create(context, it.resId).apply {
                isLooping = true
                start()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            musicPlayer?.release()
        }
    }

    LaunchedEffect(hasWon, timeExpired) {
        if (hasWon || timeExpired) {
            val mediaPlayer = if (timeExpired) {
                MediaPlayer.create(context, R.raw.lose)
            } else {
                MediaPlayer.create(context, R.raw.yaay)
            }
            mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
            mediaPlayer.start()

            delay(2000)
            navController.navigate("results/$alias/$time/$gridSize/$attempts/$isChallengeMode/$hasWon")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = currentBackground.resourceId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(Color(0xAA000000), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (musicPlayer?.isPlaying == true) {
                                musicPlayer?.pause()
                            } else {
                                musicPlayer?.start()
                            }
                            isPlaying = !isPlaying
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(if (isPlaying) "🔇" else "🔊", color = Color.White)
                    }
                }

                Text("Jugador: $alias", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                if (useTimer) {
                    Text("Tiempo: $time", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("¡A jugar!", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cards.size) { index ->
                        val card = cards[index]
                        CardView(
                            card = card,
                            onClick = { viewModel.flipCard(card) },
                            style = currentCardStyle.preview
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { viewModel.resetGame(gridSize) }) {
                    Text("🔄 Reiniciar partida", color = Color.White)
                }

                Text("Intentos: $attempts", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CardView(card: MemoryCard, onClick: () -> Unit, style: String) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .padding(4.dp)
            .clickable(enabled = !card.isMatched) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Surface(tonalElevation = 4.dp) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f
                    },
                contentAlignment = Alignment.Center
            ) {
                if (rotation >= 90f) {
                    Text(card.content, style = MaterialTheme.typography.headlineMedium)
                } else {
                    Text(style, style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}
