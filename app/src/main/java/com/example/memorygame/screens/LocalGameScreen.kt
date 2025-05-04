package com.example.memorygame.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.memorygame.R
import com.example.memorygame.logic.GameViewModel
import com.example.memorygame.model.MemoryCard

@Composable
fun LocalGameScreen(
    navController: NavController,
    pairCount: Int,
    alias1: String,
    alias2: String,
    viewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current

    val currentPlayer by viewModel.currentPlayer
    val scorePlayer1 by viewModel.player1Score
    val scorePlayer2 by viewModel.player2Score
    val currentCardStyle = viewModel.getActiveCardStyle()
    val currentBackground = viewModel.getActiveBackground()

    LaunchedEffect(Unit) {
        viewModel.startLocalGame(pairCount)
    }

    LaunchedEffect(viewModel.hasWon.value) {
        if (viewModel.hasWon.value) {
            navController.navigate("local_results/${alias1}/${alias2}/${viewModel.player1Score.value}/${viewModel.player2Score.value}")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = currentBackground.resourceId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000), shape = RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Turno: ${if (currentPlayer == 1) alias1 else alias2}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("$alias1: $scorePlayer1", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                    Text("$alias2: $scorePlayer2", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.cards.size) { index ->
                        val card = viewModel.cards[index]
                        CardViewLocal(
                            card = card,
                            onClick = { viewModel.flipCardLocal(card) },
                            style = currentCardStyle.preview
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { navController.popBackStack() }) {
                    Text("⬅️ Volver")
                }
            }
        }
    }
}

@Composable
fun CardViewLocal(card: MemoryCard, onClick: () -> Unit, style: String?) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFaceUp || card.isMatched) 180f else 0f,
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .padding(4.dp)
            .clickable(enabled = !card.isMatched) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Surface(tonalElevation = 4.dp) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f
                    },
                contentAlignment = Alignment.Center
            ) {
                if (rotation >= 90f) {
                    Text(
                        text = card.content,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black
                    )
                } else {
                    Text(
                        text = style ?: "❓",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
