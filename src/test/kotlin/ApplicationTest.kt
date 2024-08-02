package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun tasksCanBeFoundByPriority() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Medium")

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains("Plan upcoming month", response.bodyAsText())
        assertContains("Learn movement exercise", response.bodyAsText())
    }

    @Test
    fun invalidPriorityProducesBadRequest() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Invalid")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun unusedPriorityProducesNotFound() = testApplication {
        application {
            module()
        }

        val response = client.get("/tasks/byPriority/Vital")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}