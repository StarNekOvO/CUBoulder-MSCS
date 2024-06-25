package io.initialcapacity.emailverifier.notification

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import org.slf4j.LoggerFactory
import java.net.URL


class Emailer(
    private val client: HttpClient,
    private val sendgridUrl: URL,
    private val sendgridApiKey: String,
    private val fromAddress: String,
) {
    private val logger = LoggerFactory.getLogger(Emailer::class.java)

    suspend fun send(toAddress: String, subject: String, message: String): Boolean = try {
        client.post("$sendgridUrl/v3/mail/send") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $sendgridApiKey")
                contentType(ContentType.Application.Json.withCharset(Charset.forName("utf-8")))
            }
            setBody("""
                {
                    "personalizations": [{"to":[{"email": "$toAddress"}]}],
                    "from": {"email": "$fromAddress"},
                    "subject": "$subject",
                    "content": [{
                        "type": "text/plain",
                        "value": "$message"
                    }]
                }""".trimIndent())
        }.status.isSuccess()
    } catch (e: java.net.ConnectException) {
        logger.error("Unable to notify $toAddress via $sendgridUrl (${e::class.java}: ${e.message})")
        false
    }
}
