package io.initialcapacity.emailverifier.benchmark

import com.codahale.metrics.ConsoleReporter
import com.codahale.metrics.MetricRegistry
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime


class Benchmark(
    private val registrationUrl: String,
    private val registrationCount: Int,
    private val requestWorkerCount: Int,
    private val registrationWorkerCount: Int,
    private val client: HttpClient,
) {
    private val logger = LoggerFactory.getLogger(Benchmark::class.java)
    private val metrics = MetricRegistry()

    private val confirmations = Channel<Confirmation>(registrationCount)
    private val emails = Channel<String>(registrationCount)
    private val reporter = ConsoleReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build()

    suspend fun start(scope: CoroutineScope): Duration {
        logger.info("starting benchmark with $registrationCount registrations")

        launchRequestWorkers(scope)
        launchRegistrationWorkers(scope)
        startReporter(scope)

        emails.generate(registrationCount)

        return measureTime {
            while (metrics.counter("registration - total").count < registrationCount) {
                delay(100.milliseconds)
            }
        }.also { duration ->
            stop()
            logger.info("benchmark finished in $duration")
        }
    }

    suspend fun processConfirmation(message: String) {
        val jsonBody = Json.parseToJsonElement(message)

        val email = jsonBody.jsonObject["personalizations"]?.jsonArray?.get(0)?.jsonObject?.get("to")
            ?.jsonArray?.get(0)?.jsonObject?.get("email")?.jsonPrimitive?.content
        val body = jsonBody.jsonObject["content"]?.jsonArray?.get(0)?.jsonObject?.get("value")?.jsonPrimitive?.content
        val confirmationCode = UUID.fromString(body?.subSequence(26, 62).toString())

        confirmations.send(Confirmation(email!!, confirmationCode))
    }

    private fun launchRequestWorkers(scope: CoroutineScope) {
        repeat(requestWorkerCount) {
            val worker = RegistrationRequestWorker(registrationUrl, client, metrics)
            scope.launch {
                worker.listen(emails)
            }
        }
    }

    private fun launchRegistrationWorkers(scope: CoroutineScope) {
        repeat(registrationWorkerCount) {
            val worker = RegistrationWorker(registrationUrl, client, metrics)
            scope.launch {
                worker.listen(confirmations)
            }
        }
    }

    private fun startReporter(scope: CoroutineScope) {
        scope.launch {
            reporter.start(1, TimeUnit.SECONDS)
        }
    }

    private fun stop() {
        reporter.stop()
        emails.close()
        confirmations.close()
    }
}

private suspend fun Channel<String>.generate(count: Int) = repeat(count) {
    send("${UUID.randomUUID()}@example.com")
}
