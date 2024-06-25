package test.initialcapacity.emailverifier.testsupport

import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlin.test.assertEquals

fun assertJsonEquals(expected: String, actual: String?) {
    val expectedJson = parseToJsonElement(expected)
    val actualJson = parseToJsonElement(actual ?: "null")

    assertEquals(expectedJson, actualJson,
        "Expected\n$expectedJson\n to equal \n$actualJson\n")
}