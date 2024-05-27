package com.todolist.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.runtime.mutableStateOf

@RunWith(AndroidJUnit4::class)
class TaskTagDropdownTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTaskTagDropdownSelection() {
        // Define mutable state for selected tag
        val selectedTag = mutableStateOf("All")

        // Set the content for the test
        composeTestRule.setContent {
            TaskTagDropdown(
                selectedTag = selectedTag.value,
                onTagSelected = { selectedTag.value = it }
            )
        }

        // Verify initial state
        composeTestRule.onNodeWithText("All").assertExists("Initial tag not found")

        // Open the dropdown menu
        composeTestRule.onNodeWithText("All").performClick()
        composeTestRule.waitForIdle()

        // Select a different tag
        composeTestRule.onNodeWithText("Work").performClick()
        composeTestRule.waitForIdle()

        // Verify the selected tag has changed
        composeTestRule.onNodeWithText("Work").assertExists("Tag was not updated to 'Work'")
        assert(selectedTag.value == "Work") { "Selected tag value is not 'Work'" }

        // Open the dropdown menu again
        composeTestRule.onNodeWithText("Work").performClick()
        composeTestRule.waitForIdle()

        // Select another tag
        composeTestRule.onNodeWithText("Health").performClick()
        composeTestRule.waitForIdle()

        // Verify the selected tag has changed
        composeTestRule.onNodeWithText("Health").assertExists("Tag was not updated to 'Personal'")
        assert(selectedTag.value == "Health") { "Selected tag value is not 'Health'" }
    }
}