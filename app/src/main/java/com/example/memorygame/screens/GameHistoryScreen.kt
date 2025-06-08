package com.example.memorygame.screens

import GameDetailContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memorygame.logic.GameViewModel
import getDeviceType
import com.example.memorygame.model.PlayedGameEntity



@Composable
fun GameHistoryScreen(
    navController: NavController,
    viewModel: GameViewModel
) {
    val history by viewModel.gameHistory.collectAsState()
    val sortedHistory = history.sortedByDescending { it.date }
    val deviceType = getDeviceType()
    val selectedGame by viewModel.selectedGame

    if (deviceType == DeviceType.TABLET) {
        // Bi-panel: Lista + detalle
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            // Panel izquierdo: lista
            Column(
                modifier = Modifier.width(280.dp).fillMaxHeight()
            ) {
                Text("Partidas jugadas", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                LazyColumn {
                    if (sortedHistory.isEmpty()) {
                        item {
                            Text("No hay partidas jugadas", modifier = Modifier.padding(32.dp))
                        }
                    } else {
                        itemsIndexed(sortedHistory) { index, game ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable {
                                        if (deviceType == DeviceType.TABLET) {
                                            viewModel.selectGame(game)
                                        } else {
                                            navController.navigate("gameDetail/$index")
                                        }
                                    }
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("${game.alias} - ${game.gridSize}x4")
                                    Text("Modo: ${game.mode}")
                                    val tiempoStr = if (game.time > 0) " - Tiempo: ${game.time}s" else ""
                                    Text("Intentos: ${game.attempts}$tiempoStr")
                                    Text(if (game.hasWon) "Victoria" else "Derrota")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.width(24.dp))

            // Panel derecho: detalle + volver
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0x22FFFFFF), shape = RoundedCornerShape(16.dp))
                ) {
                    if (selectedGame != null) {
                        GameDetailContent(game = selectedGame!!)
                    } else {
                        Text("Selecciona una partida", modifier = Modifier.align(Alignment.Center))
                    }
                }

                Spacer(Modifier.height(16.dp))

                var showDialog by remember { mutableStateOf(false) }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("¿Borrar historial?") },
                        text = { Text("Esta acción eliminará todas las partidas guardadas. ¿Estás seguro?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.clearHistory()
                                    showDialog = false
                                }
                            ) {
                                Text("Sí")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("🗑️ Limpiar historial")
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("menu") {
                            popUpTo("menu") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⬅️ Volver")
                }
            }
        }
    }else {
        // Mono-panel mejorado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Partidas jugadas", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (sortedHistory.isEmpty()) {
                    item {
                        Text("No hay partidas jugadas", modifier = Modifier.padding(32.dp))
                    }
                } else {
                    itemsIndexed(sortedHistory) { index, game ->                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable {
                                    navController.navigate("gameDetail/$index")
                                }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("${game.alias} - ${game.gridSize}x4")
                                Text("Modo: ${game.mode}")
                                val tiempoStr = if (game.time > 0) " - Tiempo: ${game.time}s" else ""
                                Text("Intentos: ${game.attempts}$tiempoStr")
                                Text(if (game.hasWon) "Victoria" else "Derrota")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            var showDialog by remember { mutableStateOf(false) }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("¿Borrar historial?") },
                    text = { Text("Esta acción eliminará todas las partidas guardadas. ¿Estás seguro?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.clearHistory()
                                showDialog = false
                            }
                        ) {
                            Text("Sí")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("🗑️ Limpiar historial")
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⬅️ Volver")
            }
        }
    }
}