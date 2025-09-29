package io.initialcapacity.emailverifier.benchmark

import com.codahale.metrics.MetricRegistry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.channels.ReceiveChannel

class RegistrationWorker(
    private val registrationUrl: String,
    private val client: HttpClient,
    private val metrics: MetricRegistry,
) {
    suspend fun listen(confirmations: ReceiveChannel<Confirmation>) {
        for (confirmation in confirmations) {
            register(confirmation)
        }
    }

    private suspend fun register(confirmation: Confirmation) = try {
        val response = client.post("$registrationUrl/register") {
            headers { contentType(ContentType.Application.Json) }
            setBody("""{"email": "${confirmation.email}", "confirmationCode": "${confirmation.code}"}""")
        }

        if (response.status.isSuccess()) {
            metrics.counter("registration - success").inc()
            metrics.counter("registration - total").inc()
        } else {
            metrics.counter("registration - failure").inc()
            metrics.counter("registration - total").inc()
        }
    } catch (e: java.net.ConnectException) {
        metrics.counter("registration - failure").inc()
        metrics.counter("registration - total").inc()
    }
}
