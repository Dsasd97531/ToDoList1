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
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class SortDialogTest {

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
        tags = listOf("Personal"),
        priority = 3,
        isStarred = true,
        isDone = true
    )
    private val task3 = Task(
        id = 3,
        title = "Task 3",
        description = "Description 3",
        date = System.currentTimeMillis() - 1000,
        tags = listOf("Personal"),
        priority = 2,
        isStarred = true,
        isDone = false
    )

    @Before
    fun setUp() {
        // Mock TaskDao methods
        coEvery { taskDao.getAllTasks() } returns listOf(task1, task2, task3)
        coEvery { taskDao.insert(any()) } just Runs
        coEvery { taskDao.update(any()) } just Runs
        coEvery { taskDao.getTasksSortedByPriorityAsc() } returns listOf(task1, task3, task2)
        coEvery { taskDao.getTasksSortedByPriorityDesc() } returns listOf(task2, task3, task1)
        coEvery { taskDao.getTasksSortedByDateAsc() } returns listOf(task3, task1, task2)
        coEvery { taskDao.getTasksSortedByDateDesc() } returns listOf(task2, task1, task3)

        // Initialize ViewModel
        taskViewModel = TaskViewModel(ApplicationProvider.getApplicationContext())

        // Ensure tasks are loaded into the ViewModel
        taskViewModel.insertTask(task1)
        taskViewModel.insertTask(task2)
        taskViewModel.insertTask(task3)
        testScheduler.advanceUntilIdle() // Ensure tasks are loaded before continuing
    }

    @Test
    fun testSortByPriorityAscending() = runTest(testScheduler, timeout = 120.seconds) {
        // Define mutable states for sort dialog
        val showSortDialog = mutableStateOf(true)
        val sortOption = mutableStateOf("Priority")
        val sortAscending = mutableStateOf(true)

        // Set the content for the test
        composeTestRule.setContent {
            SortDialog(
                showDialog = showSortDialog,
                sortOption = sortOption,
                sortAscending = sortAscending,
                onSortOptionSelected = { option, ascending ->
                    when (option) {
                        "Priority" -> taskViewModel.sortTasksByPriority(ascending)
                        "Date" -> taskViewModel.sortTasksByDate(ascending)
                    }
                }
            )
        }

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Priority Ascending").performClick()
        composeTestRule.waitForIdle()
        testScheduler.advanceUntilIdle()
        val sortedTasks = taskViewModel.tasks.value
        println("Sorted tasks by Priority Ascending: $sortedTasks")
        assert(sortedTasks == listOf(task1, task3, task2)) { "Tasks are not sorted by priority ascending" }
    }

    @Test
    fun testSortByPriorityDescending() = runTest(testScheduler, timeout = 120.seconds) {
        // Define mutable states for sort dialog
        val showSortDialog = mutableStateOf(true)
        val sortOption = mutableStateOf("Priority")
        val sortAscending = mutableStateOf(false)

        // Set the content for the test
        composeTestRule.setContent {
            SortDialog(
                showDialog = showSortDialog,
                sortOption = sortOption,
                sortAscending = sortAscending,
                onSortOptionSelected = { option, ascending ->
                    when (option) {
                        "Priority" -> taskViewModel.sortTasksByPriority(ascending)
                        "Date" -> taskViewModel.sortTasksByDate(ascending)
                    }
                }
            )
        }

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Priority Descending").performClick()
        composeTestRule.waitForIdle()
        testScheduler.advanceUntilIdle()
        val sortedTasks = taskViewModel.tasks.value
        println("Sorted tasks by Priority Descending: $sortedTasks")
        assert(sortedTasks == listOf(task2, task3, task1)) { "Tasks are not sorted by priority descending" }
    }

    @Test
    fun testSortByDateAscending() = runTest(testScheduler, timeout = 120.seconds) {
        // Define mutable states for sort dialog
        val showSortDialog = mutableStateOf(true)
        val sortOption = mutableStateOf("Date")
        val sortAscending = mutableStateOf(true)

        // Set the content for the test
        composeTestRule.setContent {
            SortDialog(
                showDialog = showSortDialog,
                sortOption = sortOption,
                sortAscending = sortAscending,
                onSortOptionSelected = { option, ascending ->
                    when (option) {
                        "Priority" -> taskViewModel.sortTasksByPriority(ascending)
                        "Date" -> taskViewModel.sortTasksByDate(ascending)
                    }
                }
            )
        }

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Date Ascending").performClick()
        composeTestRule.waitForIdle()
        testScheduler.advanceUntilIdle()
        val sortedTasks = taskViewModel.tasks.value
        println("Sorted tasks by Date Ascending: $sortedTasks")
        assert(sortedTasks == listOf(task3, task1, task2)) { "Tasks are not sorted by date ascending" }
    }

    @Test
    fun testSortByDateDescending() = runTest(testScheduler, timeout = 120.seconds) {
        // Define mutable states for sort dialog
        val showSortDialog = mutableStateOf(true)
        val sortOption = mutableStateOf("Date")
        val sortAscending = mutableStateOf(false)

        // Set the content for the test
        composeTestRule.setContent {
            SortDialog(
                showDialog = showSortDialog,
                sortOption = sortOption,
                sortAscending = sortAscending,
                onSortOptionSelected = { option, ascending ->
                    when (option) {
                        "Priority" -> taskViewModel.sortTasksByPriority(ascending)
                        "Date" -> taskViewModel.sortTasksByDate(ascending)
                    }
                }
            )
        }

        // Perform UI interactions and assertions
        composeTestRule.onNodeWithText("Date Descending").performClick()
        composeTestRule.waitForIdle()
        testScheduler.advanceUntilIdle()
        val sortedTasks = taskViewModel.tasks.value
        println("Sorted tasks by Date Descending: $sortedTasks")
        assert(sortedTasks == listOf(task2, task1, task3)) { "Tasks are not sorted by date descending" }
    }
}
