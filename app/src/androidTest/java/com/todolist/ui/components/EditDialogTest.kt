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
import androidx.test.core.app.ApplicationProvider
import com.todolist.data.TaskDao
import com.todolist.model.Task
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.slot
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlin.time.Duration.Companion.seconds
import com.todolist.data.TaskRepository


@RunWith(AndroidJUnit4::class)
class EditDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testScheduler = TestCoroutineScheduler()

    // Mock repository and ViewModel
    private val taskDao = mockk<TaskDao>(relaxed = true)
    private val taskRepository = TaskRepository(taskDao)
    private val taskViewModel = TaskViewModel(ApplicationProvider.getApplicationContext())

    // Sample task
    private val sampleTask = Task(
        id = 1,
        title = "Initial Task",
        description = "Initial Description",
        date = System.currentTimeMillis(),
        tags = listOf("Work"),
        priority = 2,
        isStarred = false,
        isDone = false
    )

    // Add task to repository and verify update
    @Test
    fun testEditTaskDialog() = runTest(testScheduler, timeout = 120.seconds) {
        // Add task to repository
        coEvery { taskDao.getAllTasks() } returns listOf(sampleTask)
        coEvery { taskDao.update(any()) } just Runs

        // Define mutable states to pass to the EditDialog
        val newTaskTitle = mutableStateOf(sampleTask.title)
        val newTaskDescription = mutableStateOf(sampleTask.description)
        val newTaskDate = mutableStateOf<Long?>(sampleTask.date)
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Set the content for the test
        composeTestRule.setContent {
            EditDialog(
                task = sampleTask,
                showDialog = showDialog,
                newTaskTitle = newTaskTitle,
                newTaskDescription = newTaskDescription,
                newTaskDate = newTaskDate,
                newTaskTags = newTaskTags,
                initialIsDone = sampleTask.isDone,
                newTaskPriority = newTaskPriority,
                taskRepository = taskRepository,
                taskViewModel = taskViewModel,
                initialIsStarred = sampleTask.isStarred
            )
        }

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Task Title").assertExists("Task Title text field not found")
        composeTestRule.onNodeWithText("Task Title").performTextClearance()
        composeTestRule.onNodeWithText("Task Title").performTextInput("Updated Task")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Task Description").assertExists("Task Description text field not found")
        composeTestRule.onNodeWithText("Task Description").performTextClearance()
        composeTestRule.onNodeWithText("Task Description").performTextInput("Updated Description")
        composeTestRule.waitForIdle()

        // Capture the values before clicking OK
        val taskTitleBeforeClick = newTaskTitle.value
        val taskDescriptionBeforeClick = newTaskDescription.value
        val taskDateBeforeClick = newTaskDate.value

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Advance the dispatcher until all tasks are completed
        testScheduler.advanceUntilIdle()

        // Check if the dialog is dismissed
        assert(!showDialog.value) { "Dialog should be dismissed after clicking OK" }

        // Capture the task passed to updateTask
        val taskSlot = slot<Task>()
        coVerify { taskDao.update(capture(taskSlot)) }

        // Verify the captured task
        val capturedTask = taskSlot.captured
        assert(capturedTask.title == "Updated Task") { "Title does not match" }
        assert(capturedTask.description == "Updated Description") { "Description does not match" }
        assert(capturedTask.date == taskDateBeforeClick) { "Date does not match" }
    }

    // Test if the dialog does not close if the title is empty
    @Test
    fun testDialogDoesNotCloseIfTitleIsEmpty() = runTest(testScheduler, timeout = 120.seconds) {
        // Add task to repository
        coEvery { taskDao.getAllTasks() } returns listOf(sampleTask)
        coEvery { taskDao.update(any()) } just Runs

        // Define mutable states to pass to the EditDialog
        val newTaskTitle = mutableStateOf("")
        val newTaskDescription = mutableStateOf(sampleTask.description)
        val newTaskDate = mutableStateOf<Long?>(sampleTask.date)
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Set the content for the test
        composeTestRule.setContent {
            EditDialog(
                task = sampleTask,
                showDialog = showDialog,
                newTaskTitle = newTaskTitle,
                newTaskDescription = newTaskDescription,
                newTaskDate = newTaskDate,
                newTaskTags = newTaskTags,
                initialIsDone = sampleTask.isDone,
                newTaskPriority = newTaskPriority,
                taskRepository = taskRepository,
                taskViewModel = taskViewModel,
                initialIsStarred = sampleTask.isStarred
            )
        }

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Task Title").assertExists("Task Title text field not found")
        composeTestRule.onNodeWithText("Task Title").performTextClearance()
        composeTestRule.waitForIdle()

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Check if the dialog is still open
        assert(showDialog.value) { "Dialog should not be dismissed with an empty title" }
    }

    // Test if the dialog does not close if the description is empty
    @Test
    fun testDialogDoesNotCloseIfDescriptionIsEmpty() = runTest(testScheduler, timeout = 120.seconds) {
        // Add task to repository
        coEvery { taskDao.getAllTasks() } returns listOf(sampleTask)
        coEvery { taskDao.update(any()) } just Runs

        // Define mutable states to pass to the EditDialog
        val newTaskTitle = mutableStateOf(sampleTask.title)
        val newTaskDescription = mutableStateOf("")
        val newTaskDate = mutableStateOf<Long?>(sampleTask.date)
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Set the content for the test
        composeTestRule.setContent {
            EditDialog(
                task = sampleTask,
                showDialog = showDialog,
                newTaskTitle = newTaskTitle,
                newTaskDescription = newTaskDescription,
                newTaskDate = newTaskDate,
                newTaskTags = newTaskTags,
                initialIsDone = sampleTask.isDone,
                newTaskPriority = newTaskPriority,
                taskRepository = taskRepository,
                taskViewModel = taskViewModel,
                initialIsStarred = sampleTask.isStarred
            )
        }

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Task Description").assertExists("Task Description text field not found")
        composeTestRule.onNodeWithText("Task Description").performTextClearance()
        composeTestRule.waitForIdle()

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Check if the dialog is still open
        assert(showDialog.value) { "Dialog should not be dismissed with an empty description" }
    }

    // Test if the dialog does not close if the date is null
    @Test
    fun testDialogDoesNotCloseIfDateIsNull() = runTest(testScheduler, timeout = 120.seconds) {
        // Add task to repository
        coEvery { taskDao.getAllTasks() } returns listOf(sampleTask)
        coEvery { taskDao.update(any()) } just Runs

        // Define mutable states to pass to the EditDialog
        val newTaskTitle = mutableStateOf(sampleTask.title)
        val newTaskDescription = mutableStateOf(sampleTask.description)
        val newTaskDate = mutableStateOf<Long?>(null)
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Set the content for the test
        composeTestRule.setContent {
            EditDialog(
                task = sampleTask,
                showDialog = showDialog,
                newTaskTitle = newTaskTitle,
                newTaskDescription = newTaskDescription,
                newTaskDate = newTaskDate,
                newTaskTags = newTaskTags,
                initialIsDone = sampleTask.isDone,
                newTaskPriority = newTaskPriority,
                taskRepository = taskRepository,
                taskViewModel = taskViewModel,
                initialIsStarred = sampleTask.isStarred
            )
        }

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Select Date and Time").assertExists("Select Date and Time button not found")
        composeTestRule.waitForIdle()

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Check if the dialog is still open
        assert(showDialog.value) { "Dialog should not be dismissed with a null date" }
    }
}