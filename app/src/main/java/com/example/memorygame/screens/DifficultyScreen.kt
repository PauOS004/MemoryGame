package com.example.memorygame.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import com.example.memorygame.logic.GameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DifficultyScreen(navController: NavController, viewModel: GameViewModel = viewModel()) {
    val alias = viewModel.alias.value

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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Selecciona la dificultad",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("game/$alias/3/false?isEasyMode=true") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("😊 Fácil (3x4)", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("game/$alias/4/false?isMediumMode=true") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("😐 Medio (4x4)", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("game/$alias/5/false?isHardMode=true") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("😈 Difícil (5x4)", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("game/$alias/4/true?isChallengeMode=true") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏱️ Modo Contrarreloj (4x4)", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("challenge") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔥 Modo Retador (4x4 - 6x4)", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigate("settings") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⚙️ Personalizado (hasta 10x4)", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔙 Volver", color = Color.White)
            }
        }
    }
}