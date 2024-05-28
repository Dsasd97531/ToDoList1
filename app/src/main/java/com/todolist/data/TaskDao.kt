package com.todolist.data

import androidx.room.*
import com.todolist.model.Task

@Dao
interface TaskDao {

    // Query to get all tasks from the database
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    // Insert a new task into the database, replacing if there's a conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    // Update an existing task in the database
    @Update
    suspend fun update(task: Task)

    // Delete a task from the database
    @Delete
    suspend fun delete(task: Task)

    // Query to get all tasks sorted by priority in ascending order
    @Query("SELECT * FROM tasks ORDER BY priority ASC")
    fun getTasksSortedByPriorityAsc(): List<Task>

    // Query to get all tasks sorted by priority in descending order
    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    fun getTasksSortedByPriorityDesc(): List<Task>

    // Query to get all tasks sorted by date in ascending order
    @Query("SELECT * FROM tasks ORDER BY date ASC")
    fun getTasksSortedByDateAsc(): List<Task>

    // Query to get all tasks sorted by date in descending order
    @Query("SELECT * FROM tasks ORDER BY date DESC")
    fun getTasksSortedByDateDesc(): List<Task>
}