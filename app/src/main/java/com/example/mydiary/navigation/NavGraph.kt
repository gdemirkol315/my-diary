package com.example.mydiary.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Entry : Screen("entry")
}
