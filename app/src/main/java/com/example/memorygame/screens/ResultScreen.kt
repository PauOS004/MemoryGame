package com.example.memorygame.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memorygame.R
import com.example.memorygame.logic.GameViewModel
import com.example.memorygame.navigation.Screens

@Composable
fun ResultScreen(
    navController: NavController,
    alias: String,
    time: Int,
    gridSize: Int,
    attempts: Int,
    isChallengeMode: Boolean,
    lostByTimeout: Boolean,
    hasWon: Boolean,
    viewModel: GameViewModel
) {
    val coinsEarned by viewModel.coinsEarned
    val coins by viewModel.coins
    val context = LocalContext.current
    val logList = viewModel.lastGameLog


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_difficulty),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
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
                    .background(Color(0xAA000000), shape = RoundedCornerShape(24.dp))
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()), // Habilita scroll vertical
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    lostByTimeout -> {
                        Text(
                            text = "⏳ ¡Se te acabó el tiempo!",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    hasWon -> {
                        Text(
                            text = "🎉 ¡Felicidades, $alias! 🎉",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("💰 Monedas ganadas: $coinsEarned", color = Color.White)
                        Text("🏦 Total acumulado: $coins", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (time > 0) {
                            Text("⏱️ Tiempo: $time segundos", color = Color.White)
                        }
                        Text("🧠 Intentos: $attempts", color = Color.White)
                        Text("🔢 Tamaño: ${gridSize}x4", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (hasWon) {
                    Button(
                        onClick = {
                            val message = buildString {
                                appendLine("🏆 Resultado de partida Memory Game")
                                appendLine("📅 Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date())}")
                                appendLine("👤 Alias: $alias")
                                appendLine("🧩 Tamaño del tablero: ${gridSize}x4")
                                if (time != 0) appendLine("⏱️ Tiempo: $time segundos")
                                appendLine("🧠 Intentos: $attempts")

                                val modo = when {
                                    isChallengeMode -> "Contrarreloj"
                                    gridSize >= 6 -> "Personalizado"
                                    gridSize == 3 -> "Fácil"
                                    gridSize == 4 -> "Medio"
                                    gridSize == 5 -> "Difícil"
                                    else -> "Clásico"
                                }
                                appendLine("🎮 Modo de juego: $modo")

                                appendLine("🎯 Resultado: ${if (hasWon) "✅ Victoria" else if (lostByTimeout) "⏳ Derrota (Timeout)" else "❌ Derrota"}")
                                appendLine("💰 Monedas ganadas: $coinsEarned")
                                appendLine("🏦 Total acumulado: $coins")

                                if (logList.isNotEmpty()) {
                                    appendLine("\n📝 Registro de la partida:")
                                    logList.forEachIndexed { index, log ->
                                        appendLine("${index + 1}. $log")
                                    }
                                }


                            }

                            val subject = "🏆 Memory Game - Resultado de $alias"
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(Intent.EXTRA_SUBJECT, subject)
                                putExtra(Intent.EXTRA_TEXT, message)
                            }

                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No se encontró aplicación de email.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📨 Enviar resultado por email")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("history") {
                            popUpTo("history") { inclusive = true}
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📜 Ver historial de partidas")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate(Screens.Menu.route) {
                            popUpTo(Screens.Menu.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🏠 Volver al menú")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    (context as? Activity)?.finishAffinity()
                }) {
                    Text("❌ Salir")
                }
            }
        }
    }
}