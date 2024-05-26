package com.todolist.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.todolist.model.TaskTag
import com.todolist.viewmodel.TaskViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.runtime.mutableStateOf
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.time.Duration.Companion.seconds
@RunWith(AndroidJUnit4::class)
class TaskDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = StandardTestDispatcher()
    private val testScheduler = TestCoroutineScheduler()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testTaskDialog() = runTest(testScheduler, timeout = 120.seconds) {
        // Define mutable states to pass to the TaskDialog
        val newTaskTitle = mutableStateOf("")
        val newTaskDescription = mutableStateOf("")
        val newTaskDate = mutableStateOf<Long?>(System.currentTimeMillis()) // Assign a valid date value
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Mock TaskViewModel using MockK
        val taskViewModel = mockk<TaskViewModel>(relaxed = true) {
            every { ioDispatcher } returns testDispatcher
            every { mainDispatcher } returns testDispatcher
        }

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
        composeTestRule.onNodeWithText("Task Title").assertExists("Task Title text field not found")
        composeTestRule.onNodeWithText("Task Title").performTextInput("Test Task")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Task Description").assertExists("Task Description text field not found")
        composeTestRule.onNodeWithText("Task Description").performTextInput("This is a description")
        composeTestRule.waitForIdle()

        // Log the state of newTaskTitle and newTaskDescription for debugging
        println("newTaskTitle: ${newTaskTitle.value}")
        println("newTaskDescription: ${newTaskDescription.value}")

        // Assert that the title and description have been set correctly
        composeTestRule.onNodeWithText("Test Task").assertExists("The title text field was not updated")
        composeTestRule.onNodeWithText("This is a description").assertExists("The description text field was not updated")

        // Check if the "OK" button exists
        composeTestRule.onNodeWithText("OK").assertExists("OK button not found")

        // Log showDialog state before clicking "OK"
        println("showDialog before click: ${showDialog.value}")

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Advance the dispatcher until all tasks are completed
        testScheduler.advanceUntilIdle()

        // Log showDialog state after clicking "OK"
        println("showDialog after click: ${showDialog.value}")

        // Check if the dialog is dismissed
        assert(!showDialog.value) { "Dialog should be dismissed after clicking OK" }
    }
}
