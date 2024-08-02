package com.example.repository

import com.example.model.Priority
import com.example.model.Task

object TaskRepository {

    private val tasks = mutableListOf(
        Task("Format article", "Clean and format content", Priority.HIGH),
        Task("Plan upcoming month", "Set intentions", Priority.MEDIUM),
        Task("Learn movement exercise", "Learning second part of exercise", Priority.MEDIUM),
        Task("Clean home", "Make general cleaning", Priority.LOW)
    )

    fun allTasks(): List<Task> = tasks

    fun tasksByPriority(priority: Priority) = tasks.filter { it.priority == priority }

    fun taskByName(name: String) = tasks.find { it.name.equals(name, ignoreCase = true) }

    fun addTask(task: Task) {
        if (taskByName(task.name) != null) {
            throw IllegalStateException("You cant add task with same name")
        }
        tasks.add(task)
    }
}

