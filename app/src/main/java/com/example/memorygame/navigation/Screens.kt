package com.example.memorygame.navigation

sealed class Screens(val route: String) {
    object Menu : Screens("menu")
    object Settings : Screens("settings")
    object Help : Screens("help")
    object Shop : Screens("shop")
    object Difficulty : Screens("difficulty")
    object Achievements : Screens("achievements")
    object Game : Screens("game/{alias}/{gridSize}/{useTimer}")
    object Results : Screens("results/{alias}/{time}/{gridSize}/{attempts}/{challenge}/{hasWon}")
    object Challenge : Screens("challenge")
    object ChallengePlayer : Screens("game/ChallengePlayer/4/true")
}
