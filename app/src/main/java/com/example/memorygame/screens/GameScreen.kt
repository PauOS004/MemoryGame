package com.example.memorygame.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.example.memorygame.service.MusicService
import kotlinx.coroutines.delay
import android.widget.Toast
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import getDeviceType

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
    val deviceType = getDeviceType()
    val context = LocalContext.current
    var musicService by remember { mutableStateOf<MusicService?>(null) }
    var isPlaying by remember { mutableStateOf(true) }
    var serviceConnection by remember { mutableStateOf<ServiceConnection?>(null) }

    val cards = viewModel.cards
    val hasWon by viewModel.hasWon
    val attempts by viewModel.attempts
    val time by viewModel.time
    val timeExpired by viewModel.timeExpired

    val currentCardStyle = viewModel.getActiveCardStyle()
    val currentBackground = viewModel.getActiveBackground()

    val selectedMusic = viewModel.selectedMusicName.value
    val selectedTrack = viewModel.musicOptions.find { it.name == selectedMusic }

    // Iniciar el servicio
    LaunchedEffect(Unit) {
        val intent = Intent(context, MusicService::class.java)
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicService.MusicBinder
                musicService = binder.getService()
                selectedTrack?.let {
                    musicService?.playMusic(it.resId)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                musicService = null
            }
        }
        serviceConnection = connection
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    LaunchedEffect(hasWon, timeExpired) {
        if (hasWon || timeExpired) {
            val soundResId = if (timeExpired) R.raw.lose else R.raw.yaay
            musicService?.playSound(soundResId)

            delay(2000)
            val finalTime = time
            val finalAttempts = attempts
            val finalHasWon = hasWon
            viewModel.lastGameLog = viewModel.gameLog.toList()
            viewModel.onGameFinished()
            val route = if (finalTime != 0) {
                "results/$alias/$finalTime/$gridSize/$finalAttempts/$isChallengeMode/$finalHasWon"
            } else {
                "results/$alias/-1/$gridSize/$finalAttempts/$isChallengeMode/$finalHasWon"
            }
            navController.navigate(route) {
                popUpTo("menu")
            }
        }
    }

    LaunchedEffect(
        key1 = Triple(gridSize, alias, useTimer),
        key2 = Pair(isChallengeMode, isAchivementeChallenge),
        key3 = navController.previousBackStackEntry?.destination?.route
    ) {
        if (navController.previousBackStackEntry?.destination?.route == "menu" ||
            !viewModel.hasActiveGame() ||
            !viewModel.isStateMatching(
                gridSize = gridSize,
                alias = alias,
                useTimer = useTimer,
                isChallengeMode = isChallengeMode,
                isAchivementeChallenge = isAchivementeChallenge
            )) {

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
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            musicService?.stopMusic()
            serviceConnection?.let { connection ->
                try {
                    context.unbindService(connection)
                } catch (e: IllegalArgumentException) {
                    // Ignorar si servicio desvinculado
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = currentBackground.resourceId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (deviceType == DeviceType.TABLET) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .background(Color(0xAA000000), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Panel izquierdo: tablero de cartas
                Box(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                ) {
                    Text("Cartas: ${cards.size}", color = Color.White, modifier = Modifier.align(Alignment.TopStart).padding(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 28.dp)
                    ) {
                        items(cards.size) { index ->
                            val card = cards[index]
                            MemoryCardView(
                                card = card,
                                onClick = { viewModel.flipCard(card) },
                                style = currentCardStyle.preview
                            )
                        }
                    }
                }

                // Panel derecho: info y controles
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    InfoPanelContent(
                        alias = alias,
                        useTimer = useTimer,
                        isChallengeMode = isChallengeMode,
                        attempts = attempts,
                        time = time,
                        musicService = musicService,
                        isPlaying = isPlaying,
                        viewModel = viewModel,
                        onMusicToggle = {
                            if (musicService?.isPlaying() == true) {
                                musicService?.pauseMusic()
                            } else {
                                musicService?.resumeMusic()
                            }
                            isPlaying = !isPlaying
                        },
                        onResetGame = { viewModel.resetGame(gridSize) }
                    )
                }
            }
        } else {
            // Mono-panel (móvil): diseño actual
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .background(Color(0xAA000000), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (musicService?.isPlaying() == true) {
                                    musicService?.pauseMusic()
                                } else {
                                    musicService?.resumeMusic()
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
                        val timeText = if (isChallengeMode) {
                            "Tiempo restante: ${60 - time}s"
                        } else {
                            "Tiempo: ${time}s"
                        }
                        Text(
                            text = timeText,
                            color = if (isChallengeMode && time >= 55) Color.Red else Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text("¡A jugar!", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 60.dp, max = 100.dp)
                            .verticalScroll(rememberScrollState())
                            .background(Color(0x33000000), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("📝 Registro de la partida", color = Color.White, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            viewModel.gameLog.forEach {
                                Text(it, color = Color.White, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        ) {
                            items(cards.size) { index ->
                                val card = cards[index]
                                MemoryCardView(
                                    card = card,
                                    onClick = { viewModel.flipCard(card) },
                                    style = currentCardStyle.preview
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Intentos: $attempts",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = { viewModel.resetGame(gridSize) },
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text("🔄 Reiniciar partida", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoPanelContent(
    alias: String,
    useTimer: Boolean,
    isChallengeMode: Boolean,
    attempts: Int,
    time: Int,
    musicService: MusicService?,
    isPlaying: Boolean,
    onMusicToggle: () -> Unit,
    onResetGame: () -> Unit,
    viewModel: GameViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onMusicToggle,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(if (isPlaying) "🔇" else "🔊", color = Color.White)
            }
        }
        Text("Jugador: $alias", color = Color.White, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (useTimer) {
            val timeText = if (isChallengeMode) {
                "Tiempo restante: ${60 - time}s"
            } else {
                "Tiempo: ${time}s"
            }
            Text(
                text = timeText,
                color = if (isChallengeMode && time >= 55) Color.Red else Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(Modifier.height(8.dp))

        Text("📝 Registro de la partida", color = Color.White)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0x33000000), shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                viewModel.gameLog.forEach {
                    Text(it, color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }


        Text("¡A jugar!", color = Color.White, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Intentos: $attempts",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onResetGame,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("🔄 Reiniciar partida", color = Color.White)
        }
    }
}

@Composable
fun MemoryCardView(card: MemoryCard, onClick: () -> Unit, style: String) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        label = "rotation"
    )
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(width = 64.dp, height = 72.dp)
            .padding(2.dp)
            .clickable(enabled = !card.isMatched) {
                if (card.isFaceUp) {
                    Toast.makeText(context, "Esta carta ya está levantada", Toast.LENGTH_SHORT).show()
                } else {
                    onClick()
                }
            }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (rotation >= 90f) {
                    Text(
                        card.content,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(4.dp)
                    )
                } else {
                    Text(
                        style,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}


