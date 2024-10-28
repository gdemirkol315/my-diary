package com.example.mydiary.utils

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    fun reformatDateString(dateString: String): String {

        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(dateString) ?: return ""
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

        return outputFormat.format(date)
    }
}
