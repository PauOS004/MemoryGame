package com.example.memorygame.navigation

sealed class Screens(val route: String) {
    object Menu : Screens("menu")
    object Settings : Screens("settings")
    object Game : Screens("game/{alias}/{gridSize}/{useTimer}")
    object Results : Screens("results/{alias}/{time}/{gridSize}/{attempts}")
    object Help : Screens("help")
}
