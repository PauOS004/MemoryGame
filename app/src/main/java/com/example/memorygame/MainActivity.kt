package com.example.memorygame

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.memorygame.logic.AppDataStoreManager
import com.example.memorygame.logic.GameViewModel
import com.example.memorygame.navigation.AppNavigation
import com.example.memorygame.service.MusicService
import com.example.memorygame.service.MusicServiceManager
import com.example.memorygame.ui.theme.MemoryGameTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var musicService: MusicService? = null
    private var serviceConnection: ServiceConnection? = null
    private lateinit var appDataStoreManager: AppDataStoreManager
    private lateinit var gameViewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDataStoreManager = AppDataStoreManager(this)
        gameViewModel = GameViewModel(application)

        val intent = Intent(this, MusicService::class.java)
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MusicService.MusicBinder
                musicService = binder.getService()
                MusicServiceManager.musicService = musicService
                
                // Obtener el volumen guardado y aplicarlo
                CoroutineScope(Dispatchers.Main).launch {
                    appDataStoreManager.appDataFlow.collect { appData ->
                        musicService?.setMusicVolume(appData.volume)
                    }
                }
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                musicService = null
                MusicServiceManager.musicService = null
            }
        }
        bindService(intent, serviceConnection!!, BIND_AUTO_CREATE)

        setContent {
            MemoryGameTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavigation(navController, musicService)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let {
            unbindService(it)
            serviceConnection = null
        }
        gameViewModel.saveProgress()
    }
}

