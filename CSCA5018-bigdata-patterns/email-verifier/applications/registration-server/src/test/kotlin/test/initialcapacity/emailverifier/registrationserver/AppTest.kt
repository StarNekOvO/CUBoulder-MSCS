package test.initialcapacity.emailverifier.registrationserver

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class AppTest {
    @Test
    fun testInfo() = testApp {
        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)

        val body = parseToJsonElement(response.bodyAsText())
        assertEquals("registration server", body.jsonObject["application"]?.jsonPrimitive?.content)
    }
}
