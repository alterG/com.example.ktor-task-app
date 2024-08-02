package com.example.plugins

import com.example.model.Priority
import com.example.model.Task
import com.example.model.tasksAsTable
import com.example.repository.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Application.configureRouting() {
    routing {

        staticResources("/task-ui", "task-ui")

        route("/tasks") {

            get("/byPriority/{priority}") {
                val priorityAsText = call.parameters["priority"]
                if (priorityAsText == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val priority = Priority.valueOf(priorityAsText.toUpperCasePreservingASCIIRules())
                    val tasks = TaskRepository.tasksByPriority(priority)

                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }

                    call.respondText(
                        contentType = ContentType.parse("text/html"),
                        text = tasks.tasksAsTable()
                    )
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get {
                val tasks = TaskRepository.allTasks()
                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = tasks.tasksAsTable()
                )
            }

            get("/byName/{taskName}") {
                val searchTaskName = call.parameters["taskName"]
                if (searchTaskName == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val searchResult = TaskRepository.taskByName(searchTaskName)
                if (searchResult == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = listOf(searchResult).tasksAsTable()
                )
            }

            post {
                val fromContent = call.receiveParameters()
                val parameters = Triple(
                    fromContent["name"] ?: "",
                    fromContent["description"] ?: "",
                    fromContent["priority"] ?: "",
                )

                if (parameters.toList().any { it.isEmpty() }) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                try {
                    val priority = Priority.valueOf(parameters.third.toUpperCasePreservingASCIIRules())
                    val task = Task(parameters.first, parameters.second, priority)
                    TaskRepository.addTask(task)
                    call.respond(HttpStatusCode.NoContent)

                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }
        }
    }
}
