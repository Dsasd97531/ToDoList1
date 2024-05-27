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
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.todolist.model.Task
import com.todolist.util.priorityToInt
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlin.time.Duration.Companion.seconds
@RunWith(AndroidJUnit4::class)
class TaskDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testScheduler = TestCoroutineScheduler()

    private fun performTaskInput(
        composeTestRule: ComposeTestRule,
        taskTitle: String,
        taskDescription: String
    ) {
        composeTestRule.onNodeWithText("Task Title").assertExists("Task Title text field not found")
        composeTestRule.onNodeWithText("Task Title").performTextInput(taskTitle)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Task Description").assertExists("Task Description text field not found")
        composeTestRule.onNodeWithText("Task Description").performTextInput(taskDescription)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(taskTitle).assertExists("The title text field was not updated")
        composeTestRule.onNodeWithText(taskDescription).assertExists("The description text field was not updated")
    }

    private fun verifyCapturedTask(
        taskSlot: CapturingSlot<Task>,
        taskTitle: String,
        taskDescription: String,
        taskDate: Long?,
        taskTag: TaskTag,
        taskPriority: String,
        isStarred: Boolean,
        isDone: Boolean
    ) {
        val capturedTask = taskSlot.captured
        assert(capturedTask.title == taskTitle) { "Title does not match" }
        assert(capturedTask.description == taskDescription) { "Description does not match" }
        assert(capturedTask.date == taskDate) { "Date does not match" }
        assert(capturedTask.tags.contains(taskTag.displayName)) { "Tags do not match" }
        assert(capturedTask.priority == priorityToInt(taskPriority)) { "Priority does not match" }
        assert(capturedTask.isStarred == isStarred) { "isStarred does not match" }
        assert(capturedTask.isDone == isDone) { "isDone does not match" }
    }


    // Test if the task without star is added properly
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
            coEvery { insertTask(any()) } just Runs
            coEvery { loadTasks() } just Runs
            coEvery { tasks } returns MutableStateFlow(emptyList())
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
        performTaskInput(composeTestRule, "Test Task1", "This is a description1")

        // Capture the values before clicking OK
        val taskTitleBeforeClick = newTaskTitle.value
        val taskDescriptionBeforeClick = newTaskDescription.value
        val taskDateBeforeClick = newTaskDate.value
        val taskTagsBeforeClick = newTaskTags.value
        val taskPriorityBeforeClick = newTaskPriority.value

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Advance the dispatcher until all tasks are completed
        testScheduler.advanceUntilIdle()

        // Check if the dialog is dismissed
        assert(!showDialog.value) { "Dialog should be dismissed after clicking OK" }

        // Capture the task passed to insertTask
        val taskSlot = slot<Task>()
        coVerify { taskViewModel.insertTask(capture(taskSlot)) }

        // Verify the captured task
        verifyCapturedTask(
            taskSlot,
            taskTitleBeforeClick,
            taskDescriptionBeforeClick,
            taskDateBeforeClick,
            taskTagsBeforeClick,
            taskPriorityBeforeClick,
            isStarred = false,
            isDone = false
        )

        // Verify that the task list is updated
        val taskList = listOf(taskSlot.captured)
        coEvery { taskViewModel.tasks } returns MutableStateFlow(taskList)

        // Verify the task list in the ViewModel
        val updatedTaskList = taskViewModel.tasks.value
        assert(updatedTaskList.contains(taskSlot.captured)) { "The new task was not added to the list" }
    }

    // Test if the task with star is added properly
    @Test
    fun testTaskDialogWithStar() = runTest(testScheduler, timeout = 120.seconds) {
        // Define mutable states to pass to the TaskDialog
        val newTaskTitle = mutableStateOf("")
        val newTaskDescription = mutableStateOf("")
        val newTaskDate = mutableStateOf<Long?>(System.currentTimeMillis()) // Assign a valid date value
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Mock TaskViewModel using MockK
        val taskViewModel = mockk<TaskViewModel>(relaxed = true) {
            coEvery { insertTask(any()) } just Runs
            coEvery { loadTasks() } just Runs
            coEvery { tasks } returns MutableStateFlow(emptyList())
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
        performTaskInput(composeTestRule, "Test Task", "This is a description")

        // Click the star icon to set the task as starred
        composeTestRule.onNodeWithContentDescription("Is it your priority task?").performClick()
        composeTestRule.waitForIdle()

        // Capture the values before clicking OK
        val taskTitleBeforeClick = newTaskTitle.value
        val taskDescriptionBeforeClick = newTaskDescription.value
        val taskDateBeforeClick = newTaskDate.value
        val taskTagsBeforeClick = newTaskTags.value
        val taskPriorityBeforeClick = newTaskPriority.value

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Advance the dispatcher until all tasks are completed
        testScheduler.advanceUntilIdle()

        // Check if the dialog is dismissed
        assert(!showDialog.value) { "Dialog should be dismissed after clicking OK" }

        // Capture the task passed to insertTask
        val taskSlot = slot<Task>()
        coVerify { taskViewModel.insertTask(capture(taskSlot)) }

        // Verify the captured task
        verifyCapturedTask(
            taskSlot,
            taskTitleBeforeClick,
            taskDescriptionBeforeClick,
            taskDateBeforeClick,
            taskTagsBeforeClick,
            taskPriorityBeforeClick,
            isStarred = true,
            isDone = false
        )

        // Verify that the task list is updated
        val taskList = listOf(taskSlot.captured)
        coEvery { taskViewModel.tasks } returns MutableStateFlow(taskList)

        // Verify the task list in the ViewModel
        val updatedTaskList = taskViewModel.tasks.value
        assert(updatedTaskList.contains(taskSlot.captured)) { "The new task was not added to the list" }
    }

    // Test if the dialog does not close if the title is empty
    @Test
    fun testDialogDoesNotCloseIfTitleIsEmpty() = runTest(testScheduler, timeout = 120.seconds) {
        // Define mutable states to pass to the TaskDialog
        val newTaskTitle = mutableStateOf("")
        val newTaskDescription = mutableStateOf("")
        val newTaskDate = mutableStateOf<Long?>(System.currentTimeMillis()) // Assign a valid date value
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Mock TaskViewModel using MockK
        val taskViewModel = mockk<TaskViewModel>(relaxed = true) {
            coEvery { insertTask(any()) } just Runs
            coEvery { loadTasks() } just Runs
            coEvery { tasks } returns MutableStateFlow(emptyList())
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
        performTaskInput(composeTestRule, "", "This is a description")

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Check if the dialog is still open
        assert(showDialog.value) { "Dialog should not be dismissed with an empty title" }
    }

    // Test if the dialog does not close if the description is empty
    @Test
    fun testDialogDoesNotCloseIfDescriptionIsEmpty() = runTest(testScheduler, timeout = 120.seconds) {
        // Define mutable states to pass to the TaskDialog
        val newTaskTitle = mutableStateOf("")
        val newTaskDescription = mutableStateOf("")
        val newTaskDate = mutableStateOf<Long?>(System.currentTimeMillis()) // Assign a valid date value
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Mock TaskViewModel using MockK
        val taskViewModel = mockk<TaskViewModel>(relaxed = true) {
            coEvery { insertTask(any()) } just Runs
            coEvery { loadTasks() } just Runs
            coEvery { tasks } returns MutableStateFlow(emptyList())
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
        performTaskInput(composeTestRule, "Test Task", "")

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Check if the dialog is still open
        assert(showDialog.value) { "Dialog should not be dismissed with an empty description" }
    }

    // Test if the dialog does not close if the date is null
    @Test
    fun testDialogDoesNotCloseIfDateIsNull() = runTest(testScheduler, timeout = 120.seconds) {
        // Define mutable states to pass to the TaskDialog
        val newTaskTitle = mutableStateOf("")
        val newTaskDescription = mutableStateOf("")
        val newTaskDate = mutableStateOf<Long?>(null)
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Medium")
        val showDialog = mutableStateOf(true)

        // Mock TaskViewModel using MockK
        val taskViewModel = mockk<TaskViewModel>(relaxed = true) {
            coEvery { insertTask(any()) } just Runs
            coEvery { loadTasks() } just Runs
            coEvery { tasks } returns MutableStateFlow(emptyList())
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
        performTaskInput(composeTestRule, "Test Task", "This is a description")

        // Interact with the dialog's confirm button
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.waitForIdle()

        // Check if the dialog is still open
        assert(showDialog.value) { "Dialog should not be dismissed with a null date" }
    }
}