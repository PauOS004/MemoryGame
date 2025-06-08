package com.example.memorygame.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memorygame.R
import com.example.memorygame.logic.AchievementsManager
import com.example.memorygame.navigation.Screens


@Composable
fun AchievementsScreen(navController: NavController) {
    val achievements = AchievementsManager.achievements

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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🏆 Logros", style = MaterialTheme.typography.headlineMedium)

            achievements.forEach { achievement ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (achievement.unlocked)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(achievement.title, style = MaterialTheme.typography.titleMedium)
                        Text(achievement.description, style = MaterialTheme.typography.bodyMedium)
                        if (achievement.unlocked) {
                            Text("✅ Desbloqueado", color = MaterialTheme.colorScheme.primary)
                        } else {
                            Text("🔒 Bloqueado", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { navController.navigate(Screens.Menu.route) {
                popUpTo(Screens.Menu.route) { inclusive = false }
                launchSingleTop = true
            } }) {
                Text("⬅️ Volver")
            }
        }
    }
}