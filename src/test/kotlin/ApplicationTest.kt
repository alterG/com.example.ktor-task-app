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
        val responseBody = response.bodyAsText()

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(responseBody, "Plan upcoming month")
        assertContains(responseBody, "Learn movement exercise")
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

    @Test
    fun newTaskCanBeAdded() = testApplication {
        application {
            module()
        }

        val postResponse = client.post("/tasks") {
            header(
                HttpHeaders.ContentType,
                ContentType.Application.FormUrlEncoded.toString()
            )
            setBody(
                listOf(
                    "name" to "Swimming",
                    "description" to "Go to the beach",
                    "priority" to "vital"
                ).formUrlEncode()
            )
        }
        assertEquals(HttpStatusCode.NoContent, postResponse.status)

        val getResponse = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, getResponse.status)

        val getResponseBody = getResponse.bodyAsText()
        assertContains(getResponseBody, "Swimming")
        assertContains(getResponseBody, "Go to the beach")
    }
}