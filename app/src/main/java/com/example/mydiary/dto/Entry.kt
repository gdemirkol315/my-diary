package com.example.mydiary.dto

import java.util.Date

data class Entry(
    var title: String = "",
    var content: String = "",
    var date: Date = Date()
)