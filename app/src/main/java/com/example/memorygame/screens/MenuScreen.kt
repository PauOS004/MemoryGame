package com.example.memorygame.screens

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memorygame.R
import com.example.memorygame.logic.GameViewModel
import com.example.memorygame.navigation.Screens
import com.example.memorygame.service.MusicService
import com.example.memorygame.service.MusicServiceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController, viewModel: GameViewModel) {
    val context = LocalContext.current
    var musicService by remember { mutableStateOf<MusicService?>(null) }
    var isPlaying by remember { mutableStateOf(true) }
    var serviceConnection by remember { mutableStateOf<ServiceConnection?>(null) }
    val selectedMusic = viewModel.selectedMusicName.value
    val selectedTrack = viewModel.musicOptions.find { it.name == selectedMusic }

    LaunchedEffect(Unit) {
        val intent = Intent(context, MusicService::class.java)
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicService.MusicBinder
                musicService = binder.getService()
                if (musicService?.currentTrack == null) {
                    selectedTrack?.let {
                        musicService?.playMusic(it.resId)
                        MusicServiceManager.musicService = musicService
                    }
                } else {
                    isPlaying = musicService?.isPlaying() ?: true
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                musicService = null
            }
        }
        serviceConnection = connection
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
        if (navController.currentBackStackEntry?.destination?.route == Screens.Menu.route) {
            if (musicService?.currentTrack == null || musicService?.currentTrack != R.raw.mainmusic) {
                musicService?.playMusic(R.raw.mainmusic)
            } else {
                musicService?.resumeMusic()
            }
            isPlaying = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            serviceConnection?.let { connection ->
                try {
                    context.unbindService(connection)
                } catch (_: IllegalArgumentException) {
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screens.Help.route) }) {
                        Text("ℹ️", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (musicService?.isPlaying() == true) {
                            musicService?.pauseMusic()
                        } else {
                            musicService?.resumeMusic()
                        }
                        isPlaying = !isPlaying
                    }) {
                        Text(if (isPlaying) "🔇" else "🔊", style = MaterialTheme.typography.titleLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0x66000000),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                scrollBehavior = null,
                modifier = Modifier.shadow(0.dp)
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
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
                Button(onClick = { navController.navigate("history") }) {
                    Text("📜 Historial")
                }

                Spacer(modifier = Modifier.height(15.dp))
                Button(onClick = { navController.navigate("preferences") }) {
                    Text("⚙️ Preferencias")
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
}
