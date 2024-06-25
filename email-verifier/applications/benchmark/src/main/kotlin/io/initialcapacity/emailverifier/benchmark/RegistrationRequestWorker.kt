package io.initialcapacity.emailverifier.benchmark

import com.codahale.metrics.MetricRegistry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.channels.ReceiveChannel

class RegistrationRequestWorker(
    private val registrationUrl: String,
    private val client: HttpClient,
    private val metrics: MetricRegistry,
) {
    suspend fun listen(emails: ReceiveChannel<String>) {
        for (email in emails) {
            requestRegistration(email)
        }
    }

    private suspend fun requestRegistration(email: String) = try {
        val response = client.post("$registrationUrl/request-registration") {
            headers { contentType(ContentType.Application.Json) }
            setBody("""{"email": "$email"}""")
        }

        if (response.status.isSuccess()) {
            metrics.counter("request - success").inc()
        } else {
            metrics.counter("request - failure").inc()
        }
    } catch (e: java.net.ConnectException) {
        metrics.counter("request - failure").inc()
    }
}
