package com.example.memorygame.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memorygame.R
import com.example.memorygame.logic.GameViewModel
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class ShopTab(val label: String, val icon: ImageVector) {
    object Emojis : ShopTab("Emojis", Icons.Filled.Face)
    object Cards : ShopTab("Cartas", Icons.Filled.Palette)
    object Backgrounds : ShopTab("Fondos", Icons.Filled.Image)
    object Music : ShopTab("Música", Icons.Filled.MusicNote)

    companion object {
        val allTabs: List<ShopTab> = listOf(Emojis, Cards, Backgrounds, Music)
    }
}

@Composable
fun ShopScreen(navController: NavController, viewModel: GameViewModel) {
    val coins by viewModel.coins
    var selectedTabIndex by remember { mutableStateOf(0) }
    val selectedTab = remember(selectedTabIndex) { ShopTab.allTabs[selectedTabIndex] }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_difficulty),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🛍️ Tienda", style = MaterialTheme.typography.headlineMedium)
            Text("Monedas: $coins", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    is ShopTab.Emojis -> EmojiShopGrid(viewModel)
                    is ShopTab.Cards -> CardStyleShopGrid(viewModel)
                    is ShopTab.Backgrounds -> BackgroundShopGrid(viewModel)
                    is ShopTab.Music -> MusicShopGrid(viewModel)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                tonalElevation = 4.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                        ShopTab.allTabs.forEachIndexed { index, tab ->
                            NavigationBarItem(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    ) {
                        Text("⬅️ Volver al menú")
                    }
                }
            }
        }
    }
}


@Composable
fun EmojiShopGrid(viewModel: GameViewModel) {
    val context = LocalContext.current
    val coins by viewModel.coins
    val selectedTheme by viewModel.selectedThemeName

    var itemToBuy by remember { mutableStateOf<String?>(null) }
    var itemPrice by remember { mutableStateOf(0) }

    if (itemToBuy != null) {
        AlertDialog(
            onDismissRequest = { itemToBuy = null },
            confirmButton = {
                Button(onClick = {
                    viewModel.availableThemes.find { it.name == itemToBuy }?.let { theme ->
                        theme.unlocked = true
                        viewModel.coins.value -= itemPrice
                        Toast.makeText(context, "🎉 Compraste $itemToBuy", Toast.LENGTH_SHORT).show()
                    }
                    itemToBuy = null
                }) { Text("Comprar") }
            },
            dismissButton = {
                TextButton(onClick = { itemToBuy = null }) { Text("Cancelar") }
            },
            title = { Text("Confirmar compra") },
            text = { Text("¿Comprar $itemToBuy por $itemPrice monedas?") }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(viewModel.availableThemes) { theme ->
            val isSelected = theme.name == selectedTheme
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp),
                border = if (isSelected) BorderStroke(2.dp, Color.Green) else null
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(theme.name, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(theme.emojis.take(10).joinToString(" "), style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!theme.unlocked) {
                        Button(
                            onClick = {
                                itemToBuy = theme.name
                                itemPrice = theme.price
                            },
                            enabled = coins >= theme.price
                        ) {
                            Text("Comprar por ${theme.price} 💰")
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.setSelectedTheme(theme.name)
                            },
                            enabled = !isSelected
                        ) {
                            Text(if (isSelected) "En uso ✅" else "Usar tema")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CardStyleShopGrid(viewModel: GameViewModel) {
    val context = LocalContext.current
    val coins by viewModel.coins
    val selectedStyle by viewModel.selectedCardStyle
    val styles = viewModel.cardStyles

    var itemToBuy by remember { mutableStateOf<String?>(null) }
    var itemPrice by remember { mutableStateOf(0) }

    if (itemToBuy != null) {
        AlertDialog(
            onDismissRequest = { itemToBuy = null },
            confirmButton = {
                Button(onClick = {
                    styles.find { it.name == itemToBuy }?.let { style ->
                        style.unlocked = true
                        viewModel.coins.value -= itemPrice
                        Toast.makeText(context, "🎉 Compraste $itemToBuy", Toast.LENGTH_SHORT).show()
                    }
                    itemToBuy = null
                }) { Text("Comprar") }
            },
            dismissButton = {
                TextButton(onClick = { itemToBuy = null }) { Text("Cancelar") }
            },
            title = { Text("Confirmar compra") },
            text = { Text("¿Comprar $itemToBuy por $itemPrice monedas?") }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(styles) { style ->
            val isSelected = style.name == selectedStyle
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp),
                border = if (isSelected) BorderStroke(2.dp, Color.Blue) else null
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(style.name, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(style.preview, style = MaterialTheme.typography.displaySmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!style.unlocked) {
                        Button(
                            onClick = {
                                itemToBuy = style.name
                                itemPrice = style.price
                            },
                            enabled = coins >= style.price
                        ) {
                            Text("Comprar por ${style.price} 💰")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.setSelectedCardStyle(style.name) },
                            enabled = !isSelected
                        ) {
                            Text(if (isSelected) "En uso ✅" else "Usar estilo")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BackgroundShopGrid(viewModel: GameViewModel) {
    val context = LocalContext.current
    val coins by viewModel.coins
    val selectedBackground by viewModel.selectedBackgroundName
    val backgrounds = viewModel.backgroundStyles
    val unlocked = viewModel.unlockedBackgrounds

    var itemToBuy by remember { mutableStateOf<String?>(null) }
    var itemPrice by remember { mutableStateOf(0) }

    if (itemToBuy != null) {
        AlertDialog(
            onDismissRequest = { itemToBuy = null },
            confirmButton = {
                Button(onClick = {
                    viewModel.unlockBackground(itemToBuy!!)
                    viewModel.coins.value -= itemPrice
                    Toast.makeText(context, "🎉 Compraste $itemToBuy", Toast.LENGTH_SHORT).show()
                    itemToBuy = null
                }) { Text("Comprar") }
            },
            dismissButton = {
                TextButton(onClick = { itemToBuy = null }) { Text("Cancelar") }
            },
            title = { Text("Confirmar compra") },
            text = { Text("¿Comprar $itemToBuy por $itemPrice monedas?") }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(backgrounds) { bg ->
            val isSelected = bg.name == selectedBackground
            val isUnlocked = bg.price == 0 || unlocked.contains(bg.name)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp),
                border = if (isSelected) BorderStroke(2.dp, Color.Yellow) else null
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = bg.resourceId),
                        contentDescription = bg.name,
                        modifier = Modifier.height(100.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(bg.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isUnlocked) {
                        Button(onClick = {
                            itemToBuy = bg.name
                            itemPrice = bg.price
                        }, enabled = coins >= bg.price) {
                            Text("Comprar por ${bg.price} 💰")
                        }
                    } else {
                        Button(onClick = {
                            viewModel.setSelectedBackground(bg.name)
                        }, enabled = !isSelected) {
                            Text(if (isSelected) "En uso ✅" else "Usar fondo")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MusicShopGrid(viewModel: GameViewModel) {
    val context = LocalContext.current
    val coins by viewModel.coins
    val selectedMusic = viewModel.selectedMusicName.value
    val scope = rememberCoroutineScope()
    var previewPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentlyPlayingTrack by remember { mutableStateOf<String?>(null) }

    var itemToBuy by remember { mutableStateOf<String?>(null) }
    var itemPrice by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            previewPlayer?.release()
            previewPlayer = null
            currentlyPlayingTrack = null
        }
    }

    if (itemToBuy != null) {
        AlertDialog(
            onDismissRequest = { itemToBuy = null },
            confirmButton = {
                Button(onClick = {
                    viewModel.unlockMusic(itemToBuy!!)
                    viewModel.coins.value -= itemPrice
                    Toast.makeText(context, "🎉 Compraste $itemToBuy", Toast.LENGTH_SHORT).show()
                    itemToBuy = null
                }) { Text("Comprar") }
            },
            dismissButton = {
                TextButton(onClick = { itemToBuy = null }) { Text("Cancelar") }
            },
            title = { Text("Confirmar compra") },
            text = { Text("¿Comprar $itemToBuy por $itemPrice monedas?") }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(viewModel.musicOptions) { track ->
            val isSelected = track.name == selectedMusic
            val isUnlocked = viewModel.unlockedMusics.contains(track.name)

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFB3E5FC) else MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(track.name, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        try {
                            previewPlayer?.stop()
                            previewPlayer?.release()
                            previewPlayer = null
                            currentlyPlayingTrack = null

                            val player = MediaPlayer.create(context, track.resId)
                            previewPlayer = player
                            currentlyPlayingTrack = track.name
                            player.start()

                            scope.launch {
                                delay(15000)
                                if (currentlyPlayingTrack == track.name) {
                                    player.stop()
                                    player.release()
                                    previewPlayer = null
                                    currentlyPlayingTrack = null
                                }
                            }
                        } catch (_: Exception) {}
                    }) {
                        Text("🎧 Escuchar 15s")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isUnlocked) {
                        Button(
                            onClick = {
                                itemToBuy = track.name
                                itemPrice = track.price
                            },
                            enabled = coins >= track.price
                        ) {
                            Text("Comprar por ${track.price} 💰")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.setSelectedMusic(track.name) },
                            enabled = !isSelected
                        ) {
                            Text(if (isSelected) "En uso ✅" else "Usar pista")
                        }
                    }
                }
            }
        }
    }
}

