package com.example.memorygame.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memorygame.logic.GameViewModel
import com.example.memorygame.service.MusicService
import com.example.memorygame.service.MusicServiceManager

@Composable
fun PreferencesScreen(
    navController: NavController,
    gameViewModel: GameViewModel,
) {
    val appData by gameViewModel.appData.collectAsState()

    val emojiThemes = gameViewModel.availableThemes
    val backgrounds = gameViewModel.backgroundStyles
    val cardStyles = gameViewModel.cardStyles
    val musicOptions = gameViewModel.musicOptions

    val unlockedThemes = emojiThemes.filter { it.unlocked }.map { it.name }
    val unlockedBackgrounds = backgrounds.filter { it.price == 0 || gameViewModel.unlockedBackgrounds.contains(it.name) }.map { it.name }
    val unlockedCardStyles = cardStyles.filter { it.unlocked }.map { it.name }
    val unlockedMusics = musicOptions.filter { gameViewModel.unlockedMusics.contains(it.name) }.map { it.name }

    var showBlockedDialog by remember { mutableStateOf(false) }
    var blockedItemName by remember { mutableStateOf("") }

    var editableAlias by remember { mutableStateOf("") }
    var editableVolume by remember { mutableFloatStateOf(1f) }
    var editableEmojiPack by remember { mutableStateOf("") }
    var editableBackground by remember { mutableStateOf("") }
    var editableCardStyle by remember { mutableStateOf("") }
    var editableMusic by remember { mutableStateOf("") }


    LaunchedEffect(appData) {
        appData?.let { data ->
            Log.d("PreferencesScreen", "Recibido AppData: $data")
            editableAlias = data.alias
            editableVolume = data.volume
            editableEmojiPack = data.emojiPack
            editableBackground = data.backgroundPack
            editableCardStyle = data.cardStyle
            editableMusic = data.musicTrack
        }
    }

    if (showBlockedDialog) {
        AlertDialog(
            onDismissRequest = { showBlockedDialog = false },
            confirmButton = {
                TextButton(onClick = { showBlockedDialog = false }) { Text("OK") }
            },
            title = { Text("Pack bloqueado") },
            text = { Text("El paquete \"$blockedItemName\" no ha sido desbloqueado aún.") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("⚙️ Preferencias de usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = editableAlias,
            onValueChange = {
                if (it.length <= 10) editableAlias = it
            },
            label = { Text("Alias por defecto (máx. 10 caracteres)") }
        )
        Text("Máximo 10 caracteres", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(Modifier.height(18.dp))

        Text("Volumen música", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = editableVolume,
            onValueChange = {
                editableVolume = it
                gameViewModel.volume.floatValue = it
                MusicServiceManager.musicService?.setMusicVolume(it)
                Log.d("PreferencesScreen", "Volumen slider: $it")
            },
            valueRange = 0f..1f,
            steps = 9,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text("${(editableVolume * 100).toInt()}%")
        Spacer(Modifier.height(18.dp))

        Text("Pack de emojis por defecto", style = MaterialTheme.typography.titleMedium)
        DropdownMenuBox(
            options = emojiThemes.map { it.name },
            unlocked = unlockedThemes,
            selectedOption = editableEmojiPack,
            onOptionSelected = { editableEmojiPack = it },
            onBlockedAttempt = {
                blockedItemName = it
                showBlockedDialog = true
            }
        )
        Spacer(Modifier.height(18.dp))

        Text("Fondo por defecto", style = MaterialTheme.typography.titleMedium)
        DropdownMenuBox(
            options = backgrounds.map { it.name },
            unlocked = unlockedBackgrounds,
            selectedOption = editableBackground,
            onOptionSelected = { editableBackground = it },
            onBlockedAttempt = {
                blockedItemName = it
                showBlockedDialog = true
            }
        )
        Spacer(Modifier.height(18.dp))

        Text("Estilo de cartas por defecto", style = MaterialTheme.typography.titleMedium)
        DropdownMenuBox(
            options = cardStyles.map { it.name },
            unlocked = unlockedCardStyles,
            selectedOption = editableCardStyle,
            onOptionSelected = { editableCardStyle = it },
            onBlockedAttempt = {
                blockedItemName = it
                showBlockedDialog = true
            }
        )
        Spacer(Modifier.height(18.dp))

        Text("Música para jugar por defecto", style = MaterialTheme.typography.titleMedium)
        DropdownMenuBox(
            options = musicOptions.map { it.name },
            unlocked = unlockedMusics,
            selectedOption = editableMusic,
            onOptionSelected = { editableMusic = it },
            onBlockedAttempt = {
                blockedItemName = it
                showBlockedDialog = true
            }
        )
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                gameViewModel.alias.value = editableAlias
                gameViewModel.volume.floatValue = editableVolume
                gameViewModel.selectedThemeName.value = editableEmojiPack
                gameViewModel.selectedBackgroundName.value = editableBackground
                gameViewModel.selectedCardStyle.value = editableCardStyle
                gameViewModel.selectedMusicName.value = editableMusic

                gameViewModel.saveProgress()
                gameViewModel.reloadAppData()

                navController.navigateUp()
            }) {
                Text("Guardar")
            }

            Button(onClick = { navController.navigateUp() }) {
                Text("Cancelar")
            }
        }
    }
}



@Composable
fun DropdownMenuBox(
    options: List<String>,
    unlocked: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onBlockedAttempt: ((String) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedOption)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                val isUnlocked = unlocked.contains(option)
                DropdownMenuItem(
                    text = {
                        Text(option, color = if (isUnlocked) LocalContentColor.current else Color.LightGray)
                    },
                    onClick = {
                        if (isUnlocked) {
                            onOptionSelected(option)
                            expanded = false
                        } else {
                            onBlockedAttempt?.invoke(option)
                            expanded = false
                        }
                    },
                    enabled = true
                )
            }
        }
    }
}
