import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.memorygame.logic.GameViewModel
import com.example.memorygame.model.PlayedGameEntity


@Composable
fun GameDetailScreen(
    navController: NavController,
    viewModel: GameViewModel,
    gameIndex: Int
) {
    val history by viewModel.gameHistory.collectAsState()
    val sortedHistory = history.sortedByDescending { it.date }
    val game = sortedHistory.getOrNull(gameIndex)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("⬅️ Volver")
        }
        Spacer(Modifier.height(8.dp))
        if (game != null) {
            GameDetailContent(game)
        } else {
            Text("Partida no encontrada", modifier = Modifier.padding(24.dp))
        }
    }
}


@Composable
fun GameDetailContent(game: PlayedGameEntity) {
    Column(Modifier.padding(32.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text("Alias: ${game.alias}", style = MaterialTheme.typography.titleMedium)
        Text("Modo de juego: ${game.mode}")
        Text("Tamaño: ${game.gridSize}x4")
        Text("Intentos: ${game.attempts}")
        if (game.time > 0) {
            Text("Tiempo: ${game.time}s")
        }
        Text("Victoria: ${if (game.hasWon) "Sí" else "No"}")
        Text("Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(game.date))}")
        Spacer(Modifier.height(16.dp))
        Text("📝 Log de la partida:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (game.log.isEmpty()) {
            Text("No hay log disponible.")
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 250.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    game.log.split("~").forEach {
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
