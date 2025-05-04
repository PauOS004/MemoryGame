package com.example.memorygame.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memorygame.R
import com.example.memorygame.navigation.Screens

@Composable
fun HelpScreen(navController: NavController) {
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
                .padding(24.dp)
                .background(Color(0xAA000000), shape = RoundedCornerShape(24.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text("ℹ️ ¿Cómo se juega?", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Elige una dificultad o configura tu propio tablero personalizado.", color = Color.White)
            Text("El tablero contiene pares de cartas ocultas.", color = Color.White)
            Text("Toca dos cartas para revelarlas. Si coinciden, se quedan descubiertas.", color = Color.White)
            Text("Si no coinciden, se volverán a ocultar automáticamente.", color = Color.White)
            Text("Tu objetivo es encontrar todos los pares con los menos intentos posibles.", color = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            Text("🎮 Modos de juego:", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text("- Fácil / Medio / Difícil: Tableros predefinidos según dificultad.", color = Color.White)
            Text("- 🔥 Desafío: Tablero aleatorio con tiempo visible.", color = Color.White)
            Text("- ⏳ Contrarreloj: Tienes 60s para terminar el tablero.", color = Color.White)
            Text("- 🛠️ Personalizado: Tú eliges las filas y si usar cronómetro.", color = Color.White)
            Text("- 🙋‍♂️ Modo local 2 jugaodres: Modo local para jugar con parrilla personalizada de manera local 1vs1.", color = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            Text("🛍️ Tienda:", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text("Desbloquea nuevos temas de cartas (frutas, animales, emociones...).", color = Color.White)
            Text("Cambia el estilo visual de las cartas (⭐❤️🔥...).", color = Color.White)
            Text("Personaliza el fondo del juego con escenarios únicos.", color = Color.White)
            Text("Prueba música de fondo antes de comprarla (15 segundos de preview).", color = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            Text("💰 Monedas:", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text("Ganas monedas al completar niveles.", color = Color.White)
            Text("Cuanto más difícil sea el nivel, más monedas obtienes.", color = Color.White)
            Text("Puedes usarlas para comprar artículos en la tienda.", color = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            Text("🏆 Logros:", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text("Gana logros por completar partidas, jugar en modos especiales o lograr retos como el modo perfecto.", color = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            Text("🎵 Música:", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text("Puedes elegir música de fondo desde la tienda.", color = Color.White)
            Text("Se reproducirá durante la partida.", color = Color.White)
            Text("Puedes pausarla desde el botón 🔇 durante la partida o menú.", color = Color.White)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate(Screens.Menu.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🏠 Volver al menú")
            }
        }
    }
}
