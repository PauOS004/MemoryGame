package com.example.memorygame.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.memorygame.R
import com.example.memorygame.util.Constants.DEFAULT_PAR_NUM
import com.example.memorygame.util.Constants.MAX_LENGTH_ALIAS

@Composable
fun SettingsScreen(navController: NavController) {
    var alias by remember { mutableStateOf("") }
    var gridSize by remember { mutableStateOf("") }
    var useTimer by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val maxRows = 10

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_difficulty),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xAA000000), shape = MaterialTheme.shapes.medium)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Configuración de la partida",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = alias,
                    onValueChange = { alias = it },
                    label = { Text("Alias del jugador", color = Color.White) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x55000000), shape = MaterialTheme.shapes.medium)
                )

                OutlinedTextField(
                    value = gridSize,
                    onValueChange = { if (it.all { c -> c.isDigit() }) gridSize = it },
                    label = { Text("Número de filas (x4 columnas)", color = Color.White) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x55000000), shape = MaterialTheme.shapes.medium)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = useTimer, onCheckedChange = { useTimer = it })
                    Text("¿Controlar el tiempo?", color = Color.White, modifier = Modifier.padding(start = 8.dp))
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val grid = gridSize.toIntOrNull()

                        when {
                            alias.isBlank() -> {
                                errorMessage = "Por favor, ingresa un alias válido"
                            }

                            alias.length > MAX_LENGTH_ALIAS -> {
                                errorMessage = "El alias no puede tener más de 10 caracteres"
                            }

                            grid == null || grid < DEFAULT_PAR_NUM -> {
                                errorMessage = "Introduce un número de filas válido (mínimo 2)"
                            }

                            grid > maxRows -> {
                                errorMessage = "El máximo permitido es $maxRows filas"
                            }

                            else -> {
                                errorMessage = ""
                                navController.navigate("game/$alias/$grid/$useTimer?isPersMode=true")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎮 Empezar partida")
                }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⬅️ Volver")
                }
            }
        }
    }
}
