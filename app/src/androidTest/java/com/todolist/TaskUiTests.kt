package com.todolist
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.todolist.R
import com.todolist.model.Task
import com.todolist.model.TaskTag
import com.todolist.ui.components.*
import com.todolist.viewmodel.TaskViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.todolist.data.TaskRepository
import com.todolist.ui.MainActivity

@RunWith(AndroidJUnit4::class)
class TaskUiTests {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testEditDialog() = runBlocking {
        val task = Task(
            title = "Test Task",
            description = "This is a test task",
            date = System.currentTimeMillis(),
            tags = listOf("Work"),
            priority = 2,
            isStarred = false,
            isDone = false
        )
        val showDialog = mutableStateOf(true)
        val newTaskTitle = mutableStateOf(task.title)
        val newTaskDescription = mutableStateOf(task.description)
        val newTaskDate = mutableStateOf<Long?>(task.date)
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Low")

        composeTestRule.setContent {
            EditDialog(
                task = task,
                showDialog = showDialog,
                newTaskTitle = newTaskTitle,
                newTaskDescription = newTaskDescription,
                newTaskDate = newTaskDate,
                newTaskTags = newTaskTags,
                initialIsDone = task.isDone,
                newTaskPriority = newTaskPriority,
                taskRepository = mock(TaskRepository::class.java),
                taskViewModel = mock(TaskViewModel::class.java),
                initialIsStarred = task.isStarred
            )
        }

        onView(withText("Edit Task")).check(matches(isDisplayed()))
        onView(withText("Task Title")).perform(typeText("Updated Title"))
        onView(withText("Task Description")).perform(typeText("Updated Description"))
        onView(withText("OK")).perform(click())

        assert(!showDialog.value)
        assert(newTaskTitle.value == "Updated Title")
        assert(newTaskDescription.value == "Updated Description")
    }

    @Test
    fun testSearchDialog() {
        val searchQuery = mutableStateOf("")

        composeTestRule.setContent {
            SearchDialog(
                searchQuery = searchQuery.value,
                onSearchQueryChanged = { searchQuery.value = it }
            )
        }

        onView(withHint("Type to search tasks...")).check(matches(isDisplayed()))
        onView(withHint("Type to search tasks...")).perform(typeText("New search query"))

        assert(searchQuery.value == "New search query")
    }

    @Test
    fun testSortDialog() {
        val showDialog = mutableStateOf(true)
        val sortOption = mutableStateOf("Priority")
        val sortAscending = mutableStateOf(true)

        composeTestRule.setContent {
            SortDialog(
                showDialog = showDialog,
                sortOption = sortOption,
                sortAscending = sortAscending,
                onSortOptionSelected = { option, ascending ->
                    sortOption.value = option
                    sortAscending.value = ascending
                }
            )
        }

        onView(withText("Sort Tasks")).check(matches(isDisplayed()))
        onView(withText("Priority Ascending")).perform(click())

        assert(sortOption.value == "Priority")
        assert(sortAscending.value)
    }

    @Test
    fun testTaskDetailsDialog() {
        val task = Task(
            title = "Test Task",
            description = "This is a test task",
            date = System.currentTimeMillis(),
            tags = listOf("Work"),
            priority = 2,
            isStarred = false,
            isDone = false
        )
        val showDialog = mutableStateOf<Task?>(task)

        composeTestRule.setContent {
            TaskDetailsDialog(
                task = task,
                showDialog = showDialog
            )
        }

        onView(withText("Task Details")).check(matches(isDisplayed()))
        onView(withText("Title: Test Task")).check(matches(isDisplayed()))
        onView(withText("Description: This is a test task")).check(matches(isDisplayed()))
        onView(withText("Close")).perform(click())

        assert(showDialog.value == null)
    }

    @Test
    fun testTaskDialog() = runBlocking {
        val showDialog = mutableStateOf(true)
        val newTaskTitle = mutableStateOf("")
        val newTaskDescription = mutableStateOf("")
        val newTaskDate = mutableStateOf<Long?>(null)
        val newTaskTags = mutableStateOf(TaskTag.Work)
        val newTaskPriority = mutableStateOf("Low")
        val mockViewModel = mock(TaskViewModel::class.java)

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
                taskViewModel = mockViewModel
            )
        }

        onView(withText("Add New Task")).check(matches(isDisplayed()))
        onView(withText("Task Title")).perform(typeText("New Task Title"))
        onView(withText("Task Description")).perform(typeText("New Task Description"))
        onView(withText("OK")).perform(click())

        assert(!showDialog.value)
        assert(newTaskTitle.value == "New Task Title")
        assert(newTaskDescription.value == "New Task Description")
    }
}