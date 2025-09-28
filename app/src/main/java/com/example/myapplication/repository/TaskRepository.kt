package com.example.myapplication.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.data.Task
import com.example.myapplication.data.TaskDao

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): LiveData<List<Task>> = taskDao.getAllTasks()

    fun getIncompleteTasks(): LiveData<List<Task>> = taskDao.getIncompleteTasks()

    fun getCompletedTasks(): LiveData<List<Task>> = taskDao.getCompletedTasks()

    fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean) =
        taskDao.updateTaskCompletion(id, isCompleted)

    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()
}