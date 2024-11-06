package com.example.mydiary.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class DateUtilsTest {

    @Test
    fun `reformatDateString correctly formats valid date string`() {
        // Given
        val inputDate = "Wed Mar 15 14:30:00 GMT 2023"
        val expectedOutput = "15 Mar 2023"

        // When
        val result = DateUtils.reformatDateString(inputDate)

        // Then
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `reformatDateString handles different months correctly`() {
        // Test cases for different months
        val testCases = listOf(
            "Mon Jan 01 12:00:00 GMT 2023" to "01 Jan 2023",
            "Tue Feb 14 15:30:00 GMT 2023" to "14 Feb 2023",
            "Wed Mar 15 10:45:00 GMT 2023" to "15 Mar 2023",
            "Thu Apr 20 09:15:00 GMT 2023" to "20 Apr 2023",
            "Fri May 25 16:20:00 GMT 2023" to "25 May 2023",
            "Sat Jun 30 11:11:00 GMT 2023" to "30 Jun 2023",
            "Sun Jul 05 08:45:00 GMT 2023" to "05 Jul 2023",
            "Mon Aug 10 13:30:00 GMT 2023" to "10 Aug 2023",
            "Tue Sep 15 14:20:00 GMT 2023" to "15 Sep 2023",
            "Wed Oct 20 17:00:00 GMT 2023" to "20 Oct 2023",
            "Thu Nov 25 19:30:00 GMT 2023" to "25 Nov 2023",
            "Fri Dec 31 12:00:00 GMT 2023" to "31 Dec 2023"  // Changed time to avoid timezone issues
        )

        testCases.forEach { (input, expected) ->
            val result = DateUtils.reformatDateString(input)
            assertEquals("Failed for input: $input", expected, result)
        }
    }

    @Test
    fun `reformatDateString handles different years correctly`() {
        // Given
        val testCases = listOf(
            "Wed Mar 15 14:30:00 GMT 2020" to "15 Mar 2020",
            "Wed Mar 15 14:30:00 GMT 2021" to "15 Mar 2021",
            "Wed Mar 15 14:30:00 GMT 2022" to "15 Mar 2022",
            "Wed Mar 15 14:30:00 GMT 2023" to "15 Mar 2023",
            "Wed Mar 15 14:30:00 GMT 2024" to "15 Mar 2024"
        )

        testCases.forEach { (input, expected) ->
            val result = DateUtils.reformatDateString(input)
            assertEquals("Failed for input: $input", expected, result)
        }
    }

    @Test
    fun `reformatDateString handles different time zones correctly`() {
        // Given
        val testCases = listOf(
            "Wed Mar 15 12:00:00 GMT 2023" to "15 Mar 2023",
            "Wed Mar 15 12:00:00 UTC 2023" to "15 Mar 2023",
            "Wed Mar 15 12:00:00 EST 2023" to "15 Mar 2023",
            "Wed Mar 15 12:00:00 PST 2023" to "15 Mar 2023"
        )

        testCases.forEach { (input, expected) ->
            val result = DateUtils.reformatDateString(input)
            assertEquals("Failed for input: $input", expected, result)
        }
    }

    @Test
    fun `reformatDateString returns empty string for invalid date format`() {
        // Given
        val invalidInputs = listOf(
            "Invalid date",                  // Completely invalid
            "2023-03-15",                   // Wrong format
            "15/03/2023",                   // Wrong format
            "Wed Mar 15 2023",              // Missing time
            "Wed Mar 15 14:30:00 2023"      // Missing timezone
        )

        // When/Then
        invalidInputs.forEach { input ->
            val result = DateUtils.reformatDateString(input)
            assertEquals("Failed for invalid input: $input", "", result)
        }
    }

    @Test
    fun `reformatDateString handles empty string input`() {
        // Given
        val input = ""

        // When
        val result = DateUtils.reformatDateString(input)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `reformatDateString handles leap year dates correctly`() {
        // Given
        val leapYearDate = "Mon Feb 29 12:00:00 GMT 2024"
        val expectedOutput = "29 Feb 2024"

        // When
        val result = DateUtils.reformatDateString(leapYearDate)

        // Then
        assertEquals(expectedOutput, result)
    }
}