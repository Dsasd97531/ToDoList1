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
import com.todolist.ui.TaskItem
import com.todolist.ui.ToDoListScreen
import io.mockk.spyk
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class TaskItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testScheduler = TestCoroutineScheduler()

    // Mock repository and ViewModel
    private val taskDao = mockk<TaskDao>(relaxed = true)
    private val taskRepository = TaskRepository(taskDao)
    private lateinit var taskViewModel: TaskViewModel

    // Sample task
    private val task = Task(
        id = 1,
        title = "Task 1",
        description = "Description 1",
        date = System.currentTimeMillis(),
        tags = listOf("Work"),
        priority = 1,
        isStarred = false,
        isDone = false
    )

    @Before
    fun setUp() {
        // Initialize ViewModel
        taskViewModel = spyk(TaskViewModel(ApplicationProvider.getApplicationContext()), recordPrivateCalls = true)

        // Mock TaskDao methods
        coEvery { taskDao.update(any()) } just Runs
        coEvery { taskDao.delete(any()) } just Runs
    }
    @Test
    fun testEditDialogOpens() = runTest(testScheduler, timeout = 120.seconds) {
        val showDialog = mutableStateOf(false)
        val showDetailsDialog = mutableStateOf<Task?>(null)

        composeTestRule.setContent {
            TaskItem(
                task = task,
                showDialog = showDialog,
                showDetailsDialog = showDetailsDialog,
                taskViewModel = taskViewModel
            )
        }

        // Verify initial state
        composeTestRule.onNodeWithText("Task 1").assertExists("Task not found")

        // Open edit dialog
        composeTestRule.onNodeWithContentDescription("Edit Task").performClick()
        composeTestRule.waitForIdle()

        // Verify edit dialog is shown
        assert(showDialog.value) { "Edit dialog should be shown" }
    }

    @Test
    fun testShowDetailsDialog() = runTest(testScheduler, timeout = 120.seconds) {
        val showDialog = mutableStateOf(false)
        val showDetailsDialog = mutableStateOf<Task?>(null)

        composeTestRule.setContent {
            TaskItem(
                task = task,
                showDialog = showDialog,
                showDetailsDialog = showDetailsDialog,
                taskViewModel = taskViewModel
            )
        }

        // Verify initial state
        composeTestRule.onNodeWithText("Task 1").assertExists("Task not found")

        // Open details dialog
        composeTestRule.onNodeWithContentDescription("Task Details").performClick()
        composeTestRule.waitForIdle()

        // Verify details dialog is shown
        assert(showDetailsDialog.value == task) { "Details dialog should be shown" }
    }

    @Test
    fun testToggleStarStatus() = runTest(testScheduler, timeout = 120.seconds) {
        val showDialog = mutableStateOf(false)
        val showDetailsDialog = mutableStateOf<Task?>(null)

        composeTestRule.setContent {
            TaskItem(
                task = task,
                showDialog = showDialog,
                showDetailsDialog = showDetailsDialog,
                taskViewModel = taskViewModel
            )
        }

        // Verify initial state
        composeTestRule.onNodeWithText("Task 1").assertExists("Task not found")
        composeTestRule.onNodeWithContentDescription("Star Task").assertExists("Star button not found")

        // Toggle star status
        composeTestRule.onNodeWithContentDescription("Star Task").performClick()
        composeTestRule.waitForIdle()

        // Verify star status is updated
        coVerify { taskViewModel.updateTask(withArg { updatedTask ->
            assert(updatedTask.isStarred) { "Task should be starred" }
        }) }
    }

    @Test
    fun testToggleCompleteStatus() = runTest(testScheduler, timeout = 120.seconds) {
        val showDialog = mutableStateOf(false)
        val showDetailsDialog = mutableStateOf<Task?>(null)

        composeTestRule.setContent {
            TaskItem(
                task = task,
                showDialog = showDialog,
                showDetailsDialog = showDetailsDialog,
                taskViewModel = taskViewModel
            )
        }

        // Verify initial state
        composeTestRule.onNodeWithText("Task 1").assertExists("Task not found")

        // Toggle complete status
        composeTestRule.onNode(isToggleable()).performClick()
        composeTestRule.waitForIdle()

        // Verify complete status is updated
        coVerify { taskViewModel.updateTask(withArg { updatedTask ->
            assert(updatedTask.isDone) { "Task should be marked as done" }
        }) }
    }

    @Test
    fun testDeleteTask() = runTest(testScheduler, timeout = 120.seconds) {
        val showDialog = mutableStateOf(false)
        val showDetailsDialog = mutableStateOf<Task?>(null)

        composeTestRule.setContent {
            TaskItem(
                task = task,
                showDialog = showDialog,
                showDetailsDialog = showDetailsDialog,
                taskViewModel = taskViewModel
            )
        }

        // Verify initial state
        composeTestRule.onNodeWithText("Task 1").assertExists("Task not found")

        // Delete task
        composeTestRule.onNodeWithContentDescription("Delete Task").performClick()
        composeTestRule.waitForIdle()

        // Verify delete confirmation dialog is shown
        composeTestRule.onNodeWithText("Confirm Delete").assertExists("Delete confirmation dialog not found")

        // Confirm delete
        composeTestRule.onNodeWithText("Yes").performClick()
        composeTestRule.waitForIdle()

        // Verify task is deleted
        coVerify { taskViewModel.deleteTask(withArg { deletedTask ->
            assert(deletedTask == task) { "Deleted task should match the original task" }
        }) }
    }
}
