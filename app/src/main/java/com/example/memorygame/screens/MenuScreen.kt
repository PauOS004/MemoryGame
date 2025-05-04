package com.example.memorygame.screens

import android.app.Activity
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memorygame.R
import com.example.memorygame.navigation.Screens

@Composable
fun MenuScreen(navController: NavController) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }

    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.mainmusic).apply {
            isLooping = true
            setVolume(1f, 1f)
            start()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Button(
                    onClick = {
                        navController.navigate(Screens.Help.route)
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("ℹ️")
                }

                Button(
                    onClick = {
                        if (isPlaying) {
                            mediaPlayer.pause()
                        } else {
                            mediaPlayer.setVolume(1f, 1f)
                            mediaPlayer.start()
                        }
                        isPlaying = !isPlaying
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(if (isPlaying) "🔇" else "🔊")
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()), // 🔥 Añadido para evitar problemas en landscape
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "🎮 Memory Game",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.navigate("difficulty") }) {
                Text("🕹️ Jugar")
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = { navController.navigate("shop") }) {
                Text("🛍️ Tienda")
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = { navController.navigate("achievements") }) {
                Text("🏆 Logros")
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = {
                (context as? Activity)?.finishAffinity()
            }) {
                Text("❌ Salir")
            }
        }
    }
}
