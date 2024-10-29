package com.example.mydiary.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Entry : Screen("entry")
    object EntryDetail : Screen("entry_detail/{entryId}")

    fun createEntryDetailRoute(entryId: Int): String {
        return "entry_detail/$entryId"
    }
}
