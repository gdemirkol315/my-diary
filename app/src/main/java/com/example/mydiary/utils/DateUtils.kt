package com.example.mydiary.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {
    fun reformatDateString(dateString: String): String {
        if (dateString.isEmpty()) return ""

        return try {
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            val date = inputFormat.parse(dateString) ?: return ""
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

            outputFormat.format(date)
        } catch (e: ParseException) {
            ""
        }
    }
}