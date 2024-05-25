package com.todolist.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.todolist.model.TaskTag
import com.todolist.viewmodel.TaskViewModel
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.runtime.mutableStateOf

@RunWith(AndroidJUnit4::class)
class TaskDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTaskDialog() = runTest {
        // Define mutable states to pass to the TaskDialog
        val newTaskTitle = mutableStateOf("")
        val newTaskDescription = mutableStateOf("")
        val newTaskDate = mutableStateOf<Long?>(null)
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Mock TaskViewModel using MockK
        val taskViewModel = mockk<TaskViewModel>(relaxed = true)

        // Set the content for the test
        composeTestRule.setContent {
            TaskDialog(
                newTaskTitle = newTaskTitle,
                newTaskDescription = newTaskDescription,
                newTaskDate = newTaskDate,
                newTaskTags = newTaskTags,
                newTaskPriority = newTaskPriority,
                initialIsStarred = false,
                initialIsDone = false,
                showDialog = showDialog,
                taskViewModel = taskViewModel,
            )
        }

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Task Title").performTextInput("Test Task")
        composeTestRule.onNodeWithText("Task Description").performTextInput("This is a description")

        // Assert that the title and description have been set correctly
        composeTestRule.onNodeWithText("Task Title").assertTextEquals("Test Task")
        composeTestRule.onNodeWithText("Task Description").assertTextEquals("This is a description")

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()

        // Check if the dialog is dismissed
        composeTestRule.waitUntil(1000) {
            !showDialog.value
        }
    }
}