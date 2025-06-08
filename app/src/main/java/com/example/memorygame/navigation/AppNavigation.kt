package com.example.memorygame.navigation

import GameDetailScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.memorygame.logic.GameViewModel
import com.example.memorygame.screens.*
import com.example.memorygame.service.MusicService
import com.example.memorygame.util.Constants.DEFAULT_GAME_ALIAS
import com.example.memorygame.util.Constants.DEFAULT_GRID_SIZE

@Composable
fun AppNavigation(
    navController: NavHostController,
    musicService: MusicService?
) {
    val sharedViewModel: GameViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screens.Menu.route) {

        composable(Screens.Menu.route) {
            MenuScreen(navController, sharedViewModel)
        }

        composable(Screens.Settings.route) {
            SettingsScreen(navController)
        }

        composable(Screens.Shop.route) {
            ShopScreen(navController = navController, viewModel = sharedViewModel)
        }

        composable(Screens.Achievements.route) {
            AchievementsScreen(navController)
        }

        composable(
            route = Screens.Game.route + "?${NavKeys.IS_ACHIEVEMENTE_CHALLENGE}={${NavKeys.IS_ACHIEVEMENTE_CHALLENGE}}&${NavKeys.IS_EASY_MODE}={${NavKeys.IS_EASY_MODE}}&${NavKeys.IS_MEDIUM_MODE}={${NavKeys.IS_MEDIUM_MODE}}&${NavKeys.IS_HARD_MODE}={${NavKeys.IS_HARD_MODE}}&${NavKeys.IS_PERS_MODE}={${NavKeys.IS_PERS_MODE}}&${NavKeys.IS_CHALLENGE_MODE}={${NavKeys.IS_CHALLENGE_MODE}}",
            arguments = listOf(
                navArgument(NavKeys.ALIAS) { defaultValue = DEFAULT_GAME_ALIAS },
                navArgument(NavKeys.GRID_SIZE) { defaultValue = DEFAULT_GRID_SIZE.toString() },
                navArgument(NavKeys.USE_TIMER) { defaultValue = "false" },
                navArgument(NavKeys.IS_ACHIEVEMENTE_CHALLENGE) { defaultValue = "false" },
                navArgument(NavKeys.IS_EASY_MODE) { defaultValue = "false" },
                navArgument(NavKeys.IS_MEDIUM_MODE) { defaultValue = "false" },
                navArgument(NavKeys.IS_HARD_MODE) { defaultValue = "false" },
                navArgument(NavKeys.IS_PERS_MODE) { defaultValue = "false" },
                navArgument(NavKeys.IS_CHALLENGE_MODE) { defaultValue = "false" }
            )
        ) { backStackEntry ->
            val alias = backStackEntry.arguments?.getString(NavKeys.ALIAS) ?: ""
            val gridSize = backStackEntry.arguments?.getString(NavKeys.GRID_SIZE)?.toIntOrNull() ?: DEFAULT_GRID_SIZE
            val useTimer = backStackEntry.arguments?.getString(NavKeys.USE_TIMER)?.toBooleanStrictOrNull() ?: false
            val isChallengeMode = backStackEntry.arguments?.getString(NavKeys.IS_CHALLENGE_MODE)?.toBooleanStrictOrNull() ?: false
            val isAchivementeChallenge = backStackEntry.arguments?.getString(NavKeys.IS_ACHIEVEMENTE_CHALLENGE)?.toBooleanStrictOrNull() ?: false
            val isEasyMode = backStackEntry.arguments?.getString(NavKeys.IS_EASY_MODE)?.toBooleanStrictOrNull() ?: false
            val isMediumMode = backStackEntry.arguments?.getString(NavKeys.IS_MEDIUM_MODE)?.toBooleanStrictOrNull() ?: false
            val isHardMode = backStackEntry.arguments?.getString(NavKeys.IS_HARD_MODE)?.toBooleanStrictOrNull() ?: false
            val isPersMode = backStackEntry.arguments?.getString(NavKeys.IS_PERS_MODE)?.toBooleanStrictOrNull() ?: false

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
            route = "results/{alias}/{time}/{gridSize}/{attempts}/{isChallengeMode}/{hasWon}",
            arguments = listOf(
                navArgument(NavKeys.ALIAS) { type = NavType.StringType },
                navArgument(NavKeys.TIME) { type = NavType.StringType },
                navArgument(NavKeys.GRID_SIZE) { type = NavType.StringType },
                navArgument(NavKeys.ATTEMPTS) { type = NavType.StringType },
                navArgument(NavKeys.IS_CHALLENGE_MODE) { type = NavType.StringType },
                navArgument(NavKeys.HAS_WON) { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val alias = backStackEntry.arguments?.getString(NavKeys.ALIAS) ?: ""
            val time = backStackEntry.arguments?.getString(NavKeys.TIME)?.toIntOrNull() ?: 0
            val gridSize = backStackEntry.arguments?.getString(NavKeys.GRID_SIZE)?.toIntOrNull() ?: DEFAULT_GRID_SIZE
            val attempts = backStackEntry.arguments?.getString(NavKeys.ATTEMPTS)?.toIntOrNull() ?: 0
            val isChallengeMode = backStackEntry.arguments?.getString(NavKeys.IS_CHALLENGE_MODE)?.toBooleanStrictOrNull() ?: false
            val hasWon = backStackEntry.arguments?.getString(NavKeys.HAS_WON)?.toBooleanStrictOrNull() ?: false
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

        composable(Screens.Challenge.route) {
            val alias = sharedViewModel.alias.value
            val gridSize = (DEFAULT_GRID_SIZE..6).random()
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

        composable(Screens.Help.route) {
            HelpScreen(navController)
        }

        composable(Screens.Difficulty.route) {
            DifficultyScreen(navController, sharedViewModel)
        }

        composable("history") {
            GameHistoryScreen(navController, sharedViewModel)
        }

        composable(
            "gameDetail/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments?.getInt("index") ?: -1
            GameDetailScreen(navController, sharedViewModel, index)
        }

        composable("preferences") {
            PreferencesScreen(navController, sharedViewModel)
        }
    }
}
