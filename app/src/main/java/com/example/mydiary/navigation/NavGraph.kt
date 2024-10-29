package com.example.mydiary.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Entry : Screen("entry")
    object EntryDetail : Screen("entry_detail/{entryId}")
    object EntryEdit : Screen("entry/{entryId}")

    fun createEntryDetailRoute(entryId: Int): String {
        return "entry_detail/$entryId"
    }

    fun createEntryEditRoute(entryId: Int): String {
        return "entry/$entryId"
    }
}
