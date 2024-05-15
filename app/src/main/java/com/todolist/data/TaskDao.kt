package com.todolist.data

import androidx.room.*
import com.todolist.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks ORDER BY priority ASC")
    fun getTasksSortedByPriorityAsc(): List<Task>

    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    fun getTasksSortedByPriorityDesc(): List<Task>

    @Query("SELECT * FROM tasks ORDER BY date ASC")
    fun getTasksSortedByDateAsc(): List<Task>

    @Query("SELECT * FROM tasks ORDER BY date DESC")
    fun getTasksSortedByDateDesc(): List<Task>
}