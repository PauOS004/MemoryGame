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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun LocalSetupScreen(navController: NavController) {
    var alias1 by remember { mutableStateOf("") }
    var alias2 by remember { mutableStateOf("") }
    var pairCountText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

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
                    "Configuración partida local",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = alias1,
                    onValueChange = { alias1 = it.take(10) },
                    label = { Text("Alias Jugador 1 (máx 10)", color = Color.White) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x55000000), shape = MaterialTheme.shapes.medium)
                )

                OutlinedTextField(
                    value = alias2,
                    onValueChange = { alias2 = it.take(10) },
                    label = { Text("Alias Jugador 2 (máx 10)", color = Color.White) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x55000000), shape = MaterialTheme.shapes.medium)
                )

                OutlinedTextField(
                    value = pairCountText,
                    onValueChange = { pairCountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Número de parejas (10-20)", color = Color.White) },
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x55000000), shape = MaterialTheme.shapes.medium)
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val pairCount = pairCountText.toIntOrNull()
                        if (pairCount == null || pairCount !in 10..20) {
                            errorMessage = "El número de parejas debe ser entre 10 y 20."
                        } else if (alias1.isBlank() || alias2.isBlank()) {
                            errorMessage = "Ambos alias deben estar completos."
                        } else {
                            errorMessage = ""
                            val alias1Encoded = URLEncoder.encode(alias1.trim(), StandardCharsets.UTF_8.toString())
                            val alias2Encoded = URLEncoder.encode(alias2.trim(), StandardCharsets.UTF_8.toString())
                            navController.navigate("local_game/$pairCount/$alias1Encoded/$alias2Encoded")
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
