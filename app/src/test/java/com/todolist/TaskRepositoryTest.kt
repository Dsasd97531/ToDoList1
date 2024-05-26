package com.todolist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.todolist.data.TaskDao
import com.todolist.data.TaskRepository
import com.todolist.model.Task
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TaskRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val taskDao = mock(TaskDao::class.java)
    private val repository = TaskRepository(taskDao)

    @Test
    fun insertTaskTest() = runBlocking {
        val task = Task(
            title = "Test Task",
            description = "This is a test task",
            date = System.currentTimeMillis(),
            tags = listOf("Work"),
            priority = 2,
            isStarred = false,
            isDone = false
        )

        repository.insertTask(task)
        verify(taskDao, times(1)).insert(task)
    }

    @Test
    fun updateTaskTest() = runBlocking {
        val task = Task(
            title = "Test Task",
            description = "This is a test task",
            date = System.currentTimeMillis(),
            tags = listOf("Work"),
            priority = 2,
            isStarred = false,
            isDone = false
        )

        repository.updateTask(task)
        verify(taskDao, times(1)).update(task)
    }

    @Test
    fun deleteTaskTest() = runBlocking {
        val task = Task(
            title = "Test Task",
            description = "This is a test task",
            date = System.currentTimeMillis(),
            tags = listOf("Work"),
            priority = 2,
            isStarred = false,
            isDone = false
        )

        repository.deleteTask(task)
        verify(taskDao, times(1)).delete(task)
    }

    @Test
    fun getAllTasksTest() = runBlocking {
        val tasks = listOf(
            Task(
                title = "Test Task 1",
                description = "This is a test task 1",
                date = System.currentTimeMillis(),
                tags = listOf("Work"),
                priority = 1,
                isStarred = false,
                isDone = false
            ),
            Task(
                title = "Test Task 2",
                description = "This is a test task 2",
                date = System.currentTimeMillis(),
                tags = listOf("Home"),
                priority = 2,
                isStarred = true,
                isDone = false
            )
        )

        `when`(taskDao.getAllTasks()).thenReturn(tasks)

        val result = repository.getAllTasks()
        assertEquals(tasks, result)
    }

    @Test
    fun getTasksSortedByPriorityAscTest() {
        val tasks = listOf(
            Task(
                title = "Low Priority Task",
                description = "This is a low priority task",
                date = System.currentTimeMillis(),
                tags = listOf("Work"),
                priority = 1,
                isStarred = false,
                isDone = false
            ),
            Task(
                title = "High Priority Task",
                description = "This is a high priority task",
                date = System.currentTimeMillis(),
                tags = listOf("Home"),
                priority = 2,
                isStarred = true,
                isDone = false
            )
        )

        `when`(taskDao.getTasksSortedByPriorityAsc()).thenReturn(tasks)

        val result = repository.getTasksSortedByPriority(true)
        assertEquals(tasks, result)
    }

    @Test
    fun getTasksSortedByPriorityDescTest() {
        val tasks = listOf(
            Task(
                title = "High Priority Task",
                description = "This is a high priority task",
                date = System.currentTimeMillis(),
                tags = listOf("Work"),
                priority = 2,
                isStarred = true,
                isDone = false
            ),
            Task(
                title = "Low Priority Task",
                description = "This is a low priority task",
                date = System.currentTimeMillis(),
                tags = listOf("Home"),
                priority = 1,
                isStarred = false,
                isDone = false
            )
        )

        `when`(taskDao.getTasksSortedByPriorityDesc()).thenReturn(tasks)

        val result = repository.getTasksSortedByPriority(false)
        assertEquals(tasks, result)
    }

    @Test
    fun getTasksSortedByDateAscTest() {
        val tasks = listOf(
            Task(
                title = "Older Task",
                description = "This is an older task",
                date = System.currentTimeMillis() - 1000,
                tags = listOf("Work"),
                priority = 1,
                isStarred = false,
                isDone = false
            ),
            Task(
                title = "Newer Task",
                description = "This is a newer task",
                date = System.currentTimeMillis(),
                tags = listOf("Home"),
                priority = 2,
                isStarred = true,
                isDone = false
            )
        )

        `when`(taskDao.getTasksSortedByDateAsc()).thenReturn(tasks)

        val result = repository.getTasksSortedByDate(true)
        assertEquals(tasks, result)
    }

    @Test
    fun getTasksSortedByDateDescTest() {
        val tasks = listOf(
            Task(
                title = "Newer Task",
                description = "This is a newer task",
                date = System.currentTimeMillis(),
                tags = listOf("Work"),
                priority = 2,
                isStarred = true,
                isDone = false
            ),
            Task(
                title = "Older Task",
                description = "This is an older task",
                date = System.currentTimeMillis() - 1000,
                tags = listOf("Home"),
                priority = 1,
                isStarred = false,
                isDone = false
            )
        )

        `when`(taskDao.getTasksSortedByDateDesc()).thenReturn(tasks)

        val result = repository.getTasksSortedByDate(false)
        assertEquals(tasks, result)
    }
}