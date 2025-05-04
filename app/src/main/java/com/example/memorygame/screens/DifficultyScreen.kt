package com.example.memorygame.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@Composable
fun DifficultyScreen(navController: NavController) {
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Selecciona el modo de juego", style = MaterialTheme.typography.headlineMedium, color = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("game/EasyPlayer/3/false?isEasyMode=true") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fácil (3x4)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("game/MediumPlayer/4/false?isMediumMode=true") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Medio (4x4)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("game/HardPlayer/5/false?isHardMode=true") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Difícil (5x4)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("settings") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Personalizado 🛠️")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("challenge") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Modo Desafío 🎲")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("game/ChallengePlayer/4/true?isAchivementeChallenge=false&isEasyMode=false&isMediumMode=false&isHardMode=false&isPersMode=false&isChallengeMode=true")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Modo Contrarreloj ⏱️")
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("local_setup") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("👥 Modo Local (2 jugadores)")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("⬅️ Volver")
            }
        }
    }
}
