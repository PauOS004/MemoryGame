package com.example.memorygame.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.memorygame.logic.GameViewModel
import com.example.memorygame.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    val sharedViewModel: GameViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screens.Menu.route) {

        composable(Screens.Menu.route) {
            MenuScreen(navController)
        }

        composable(Screens.Settings.route) {
            SettingsScreen(navController)
        }

        composable("shop") {
            ShopScreen(navController = navController, viewModel = sharedViewModel)
        }

        composable(
            route = "game/{alias}/{gridSize}/{useTimer}?isAchivementeChallenge={isAchivementeChallenge}&isEasyMode={isEasyMode}&isMediumMode={isMediumMode}&isHardMode={isHardMode}&isPersMode={isPersMode}&isChallengeMode={isChallengeMode}",
            arguments = listOf(
                navArgument("isAchivementeChallenge") { defaultValue = "false" },
                navArgument("isEasyMode") { defaultValue = "false" },
                navArgument("isMediumMode") { defaultValue = "false" },
                navArgument("isHardMode") { defaultValue = "false" },
                navArgument("isPersMode") { defaultValue = "false" },
                navArgument("isChallengeMode") { defaultValue = "false" }
            )
        ) { backStackEntry ->
            val alias = backStackEntry.arguments?.getString("alias") ?: ""
            val gridSize = backStackEntry.arguments?.getString("gridSize")?.toIntOrNull() ?: 4
            val useTimer = backStackEntry.arguments?.getString("useTimer")?.toBooleanStrictOrNull() ?: false
            val isChallengeMode = backStackEntry.arguments?.getString("isChallengeMode")?.toBooleanStrictOrNull() ?: false
            val isAchivementeChallenge = backStackEntry.arguments?.getString("isAchivementeChallenge")?.toBooleanStrictOrNull() ?: false
            val isEasyMode = backStackEntry.arguments?.getString("isEasyMode")?.toBooleanStrictOrNull() ?: false
            val isMediumMode = backStackEntry.arguments?.getString("isMediumMode")?.toBooleanStrictOrNull() ?: false
            val isHardMode = backStackEntry.arguments?.getString("isHardMode")?.toBooleanStrictOrNull() ?: false
            val isPersMode = backStackEntry.arguments?.getString("isPersMode")?.toBooleanStrictOrNull() ?: false

            GameScreen(
                navController = navController,
                alias = alias,
                gridSize = gridSize,
                useTimer = useTimer,
                isChallengeMode = isChallengeMode,
                isAchivementeChallenge = isAchivementeChallenge,
                isEasyMode = isEasyMode,
                isMediumMode = isMediumMode,
                isHardMode = isHardMode,
                isPersMode = isPersMode,
                viewModel = sharedViewModel
            )
        }

        composable(
            route = "results/{alias}/{time}/{gridSize}/{attempts}/{challenge}/{hasWon}"
        ) { backStackEntry ->
            val alias = backStackEntry.arguments?.getString("alias") ?: ""
            val time = backStackEntry.arguments?.getString("time")?.toIntOrNull() ?: 0
            val gridSize = backStackEntry.arguments?.getString("gridSize")?.toIntOrNull() ?: 4
            val attempts = backStackEntry.arguments?.getString("attempts")?.toIntOrNull() ?: 0
            val isChallengeMode = backStackEntry.arguments?.getString("challenge")?.toBooleanStrictOrNull() ?: false
            val hasWon = backStackEntry.arguments?.getString("hasWon")?.toBooleanStrictOrNull() ?: false
            val lostByTimeout = isChallengeMode && time >= 60 && !hasWon

            ResultScreen(
                navController = navController,
                alias = alias,
                time = time,
                gridSize = gridSize,
                attempts = attempts,
                isChallengeMode = isChallengeMode,
                lostByTimeout = lostByTimeout,
                hasWon = hasWon,
                viewModel = sharedViewModel
            )
        }

        composable("challenge") {
            val alias = "Retador"
            val gridSize = (4..6).random()
            val useTimer = true
            val isChallengeMode = false
            val isAchivementeChallenge = true

            GameScreen(
                navController = navController,
                alias = alias,
                gridSize = gridSize,
                useTimer = useTimer,
                isChallengeMode = isChallengeMode,
                isAchivementeChallenge = isAchivementeChallenge,
                viewModel = sharedViewModel
            )
        }

        composable("game/ChallengePlayer/4/true?isChallengeMode=true") {
            val alias = "SpeedMaster"
            val gridSize = 4
            val useTimer = true
            val isChallengeMode = true

            GameScreen(
                navController = navController,
                alias = alias,
                gridSize = gridSize,
                useTimer = useTimer,
                isChallengeMode = isChallengeMode,
                isAchivementeChallenge = false,
                isEasyMode = false,
                isMediumMode = false,
                isHardMode = false,
                isPersMode = false,
                viewModel = sharedViewModel
            )
        }

        composable("local_setup") {
            LocalSetupScreen(navController)
        }

        composable(
            route = "local_game/{pairCount}/{alias1}/{alias2}",
            arguments = listOf(
                navArgument("pairCount") { defaultValue = "10" },
                navArgument("alias1") { defaultValue = "Jugador 1" },
                navArgument("alias2") { defaultValue = "Jugador 2" }
            )
        ) { backStackEntry ->
            val pairCount = backStackEntry.arguments?.getString("pairCount")?.toIntOrNull() ?: 10
            val alias1 = backStackEntry.arguments?.getString("alias1") ?: "Jugador 1"
            val alias2 = backStackEntry.arguments?.getString("alias2") ?: "Jugador 2"

            LocalGameScreen(
                navController = navController,
                pairCount = pairCount,
                alias1 = alias1,
                alias2 = alias2,
                viewModel = sharedViewModel
            )
        }

        composable(
            route = "local_results/{alias1}/{alias2}/{score1}/{score2}",
            arguments = listOf(
                navArgument("alias1") { defaultValue = "Jugador 1" },
                navArgument("alias2") { defaultValue = "Jugador 2" },
                navArgument("score1") { defaultValue = "0" },
                navArgument("score2") { defaultValue = "0" }
            )
        ) { backStackEntry ->
            val alias1 = backStackEntry.arguments?.getString("alias1") ?: "Jugador 1"
            val alias2 = backStackEntry.arguments?.getString("alias2") ?: "Jugador 2"
            val score1 = backStackEntry.arguments?.getString("score1")?.toIntOrNull() ?: 0
            val score2 = backStackEntry.arguments?.getString("score2")?.toIntOrNull() ?: 0

            LocalResultScreen(
                navController = navController,
                alias1 = alias1,
                alias2 = alias2,
                score1 = score1,
                score2 = score2
            )
        }

        composable(Screens.Help.route) {
            HelpScreen(navController)
        }

        composable("difficulty") {
            DifficultyScreen(navController)
        }

        composable("achievements") {
            AchievementsScreen(navController)
        }
    }
}
