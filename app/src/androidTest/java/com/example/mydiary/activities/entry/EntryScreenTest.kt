package com.example.mydiary.activities.entry

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EntryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNewEntryScreenInitialState() {
        composeTestRule.setContent {
            EntryScreen(onNavigateBack = {})
        }

        composeTestRule.onNodeWithText("New Entry").assertExists()
        composeTestRule.onNodeWithContentDescription("Home").assertExists()
        composeTestRule.onNodeWithText("Title").assertExists()
        composeTestRule.onNodeWithText("Content").assertExists()
        composeTestRule.onNodeWithText("Save").assertExists()
    }

    @Test
    fun testTextFieldsInteraction() {
        composeTestRule.setContent {
            EntryScreen(onNavigateBack = {})
        }

        composeTestRule.onNodeWithText("Title")
            .performTextInput("Test Title")
        composeTestRule.onNodeWithText("Test Title")
            .assertExists()

        composeTestRule.onNodeWithText("Content")
            .performTextInput("Test Content")
        composeTestRule.onNodeWithText("Test Content")
            .assertExists()
    }


    @Test
    fun testDateFieldIsDisabled() {
        composeTestRule.setContent {
            EntryScreen(onNavigateBack = {})
        }

        composeTestRule.onNodeWithText("Date")
            .assertExists()
            .assertIsNotEnabled()
    }

    @Test
    fun testNavigationButton() {
        var navigationCalled = false
        
        composeTestRule.setContent {
            EntryScreen(onNavigateBack = { navigationCalled = true })
        }

        composeTestRule.onNodeWithContentDescription("Home")
            .assertExists()
            .performClick()

        assert(navigationCalled) { "Navigation callback was not called" }
    }

    @Test
    fun testSaveButtonState() {
        composeTestRule.setContent {
            EntryScreen(onNavigateBack = {})
        }

        composeTestRule.onNodeWithText("Save")
            .assertExists()
            .assertIsEnabled()

        composeTestRule.onNodeWithText("Title")
            .performTextInput("Test")
        composeTestRule.onNodeWithText("Save")
            .assertIsEnabled()
    }
}
