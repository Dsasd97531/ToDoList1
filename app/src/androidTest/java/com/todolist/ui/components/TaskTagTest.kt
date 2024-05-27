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
import com.todolist.ui.ToDoListScreen
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class TaskTagTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testScheduler = TestCoroutineScheduler()

    // Mock repository and ViewModel
    private val taskDao = mockk<TaskDao>(relaxed = true)
    private val taskRepository = TaskRepository(taskDao)
    private lateinit var taskViewModel: TaskViewModel

    // Sample tasks
    private val task1 = Task(
        id = 1,
        title = "Task 1",
        description = "Description 1",
        date = System.currentTimeMillis(),
        tags = listOf("Work"),
        priority = 1,
        isStarred = false,
        isDone = false
    )
    private val task2 = Task(
        id = 2,
        title = "Task 2",
        description = "Description 2",
        date = System.currentTimeMillis() + 1000,
        tags = listOf("Family"),
        priority = 3,
        isStarred = true,
        isDone = true
    )
    private val task3 = Task(
        id = 3,
        title = "Task 3",
        description = "Description 3",
        date = System.currentTimeMillis() - 1000,
        tags = listOf("Health"),
        priority = 2,
        isStarred = true,
        isDone = false
    )

    @Before
    fun setUp() {
        // Mock TaskDao methods
        coEvery { taskDao.getAllTasks() } returns listOf(task1, task2, task3)

        // Initialize ViewModel
        taskViewModel = TaskViewModel(ApplicationProvider.getApplicationContext())

        // Ensure tasks are loaded into the ViewModel
        taskViewModel.insertTask(task1)
        taskViewModel.insertTask(task2)
        taskViewModel.insertTask(task3)
        taskViewModel.loadTasks()
        testScheduler.advanceUntilIdle() // Ensure tasks are loaded before continuing
    }

    @Test
    fun testTagFilterFunctionality() = runTest(testScheduler, timeout = 120.seconds) {
        // Set the content for the test
        composeTestRule.setContent {
            ToDoListScreen(taskViewModel = taskViewModel)
        }

        // Verify initial state
        composeTestRule.onNodeWithText("Task 1").assertExists("Task 1 not found")
        composeTestRule.onNodeWithText("Task 2").assertExists("Task 2 not found")
        composeTestRule.onNodeWithText("Task 3").assertExists("Task 3 not found")

        // Open the tag dropdown
        composeTestRule.onNodeWithText("All").performClick()
        composeTestRule.waitForIdle()

        // Select the "Work" tag
        composeTestRule.onAllNodesWithText("Work")[1].performClick()
        composeTestRule.waitForIdle()

        // Verify that only "Task 1" is shown
        composeTestRule.onNodeWithText("Task 1").assertExists("Task 1 not found after filtering by 'Work'")
        composeTestRule.onNodeWithText("Task 2").assertDoesNotExist()
        composeTestRule.onNodeWithText("Task 3").assertDoesNotExist()

        // Open the tag dropdown again
        composeTestRule.onAllNodesWithText("Work")[1].performClick()
        composeTestRule.waitForIdle()

        // Select the "Family" tag
        composeTestRule.onAllNodesWithText("Family")[0].performClick()
        composeTestRule.waitForIdle()

        // Verify that only "Task 2" is shown
        composeTestRule.onNodeWithText("Task 1").assertDoesNotExist()
        composeTestRule.onNodeWithText("Task 2").assertExists("Task 2 not found after filtering by 'Family'")
        composeTestRule.onNodeWithText("Task 3").assertDoesNotExist()

        // Open the tag dropdown again
        composeTestRule.onAllNodesWithText("Family")[0].performClick()
        composeTestRule.waitForIdle()
    }
}