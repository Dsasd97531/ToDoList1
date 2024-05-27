package com.todolist.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import io.mockk.coEvery
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlin.time.Duration.Companion.seconds
import com.todolist.data.TaskRepository
import com.todolist.ui.ToDoListScreen


@RunWith(AndroidJUnit4::class)
class TaskSearchTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testScheduler = TestCoroutineScheduler()

    // Mock repository and ViewModel
    private val taskDao = mockk<TaskDao>(relaxed = true)
    private val taskRepository = TaskRepository(taskDao)
    private val taskViewModel = TaskViewModel(ApplicationProvider.getApplicationContext())

    // Sample tasks
    private val task1 = Task(
        id = 1,
        title = "Initial Task",
        description = "Initial Description",
        date = System.currentTimeMillis(),
        tags = listOf("Work"),
        priority = 2,
        isStarred = false,
        isDone = false
    )
    private val task2 = Task(
        id = 2,
        title = "Another Task",
        description = "Another Description",
        date = System.currentTimeMillis(),
        tags = listOf("Personal"),
        priority = 3,
        isStarred = true,
        isDone = true
    )

    // Test search functionality
    @Test
    fun testSearchFunctionality() = runTest(testScheduler, timeout = 120.seconds) {
        // Add tasks to repository
        val tasks = listOf(task1, task2)
        coEvery { taskDao.getAllTasks() } returns tasks

        // Update ViewModel state
        taskViewModel.loadTasks()

        // Define mutable states for search dialog
        val searchQuery = mutableStateOf("")
        val showStarredTasksOnly = mutableStateOf(false)
        val showCompletedTasksOnly = mutableStateOf(false)

        // Set the content for the test
        composeTestRule.setContent {
            ToDoListScreen(taskViewModel = taskViewModel)
        }

        // Perform search query and verify filtered results
        searchQuery.value = "Initial"
        val filteredTasks = filterTasks(
            tasks,
            searchQuery.value,
            showStarredTasksOnly.value,
            showCompletedTasksOnly.value
        )
        assert(filteredTasks == listOf(task1)) { "Filtered tasks do not match expected results for query 'Initial'" }

        searchQuery.value = "Another"
        val filteredTasksAnother = filterTasks(
            tasks,
            searchQuery.value,
            showStarredTasksOnly.value,
            showCompletedTasksOnly.value
        )
        assert(filteredTasksAnother == listOf(task2)) { "Filtered tasks do not match expected results for query 'Another'" }

        // Test starred filter
        showStarredTasksOnly.value = true
        val filteredTasksStarred =
            filterTasks(tasks, "", showStarredTasksOnly.value, showCompletedTasksOnly.value)
        assert(filteredTasksStarred == listOf(task2)) { "Filtered tasks do not match expected results for starred filter" }

        // Test completed filter
        showCompletedTasksOnly.value = true
        val filteredTasksCompleted =
            filterTasks(tasks, "", showStarredTasksOnly.value, showCompletedTasksOnly.value)
        assert(filteredTasksCompleted == listOf(task2)) { "Filtered tasks do not match expected results for completed filter" }

        // Test combined filters
        showStarredTasksOnly.value = false
        showCompletedTasksOnly.value = true
        val filteredTasksCompletedOnly =
            filterTasks(tasks, "", showStarredTasksOnly.value, showCompletedTasksOnly.value)
        assert(filteredTasksCompletedOnly == listOf(task2)) { "Filtered tasks do not match expected results for completed filter only" }

        showStarredTasksOnly.value = true
        val filteredTasksStarredAndCompleted =
            filterTasks(tasks, "", showStarredTasksOnly.value, showCompletedTasksOnly.value)
        assert(filteredTasksStarredAndCompleted == listOf(task2)) { "Filtered tasks do not match expected results for starred and completed filter" }
    }
}