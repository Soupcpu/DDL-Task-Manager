package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Task
import com.example.myapplication.data.TaskDatabase
import com.example.myapplication.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    val allTasks: LiveData<List<Task>>
    val incompleteTasks: LiveData<List<Task>>
    val completedTasks: LiveData<List<Task>>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.getAllTasks()
        incompleteTasks = repository.getIncompleteTasks()
        completedTasks = repository.getCompletedTasks()
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    fun updateTaskCompletion(id: Long, isCompleted: Boolean) = viewModelScope.launch {
        repository.updateTaskCompletion(id, isCompleted)
    }

    fun deleteCompletedTasks() = viewModelScope.launch {
        repository.deleteCompletedTasks()
    }

    fun getTaskByIdAsync(id: Long, callback: (Task?) -> Unit) = viewModelScope.launch {
        val task = repository.getTaskById(id)
        // 使用Dispatchers.Main确保回调在主线程执行
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
            callback(task)
        }
    }
}