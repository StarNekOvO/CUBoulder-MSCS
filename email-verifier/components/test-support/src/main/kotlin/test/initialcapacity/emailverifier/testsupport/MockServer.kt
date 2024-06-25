package test.initialcapacity.emailverifier.testsupport

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.server.plugins.doublereceive.*
import kotlinx.coroutines.delay
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class MockServer(
    port: Int,
    module: Application.() -> Unit
) {
    private val calls = mutableListOf<String>()

    private val server = embeddedServer(
        factory = Jetty,
        port = port,
        module = {
            install(DoubleReceive)
            module()
            intercept(ApplicationCallPipeline.Monitoring) {
                calls.add(context.request.call.receiveText())
            }
        }
    )

    fun start() = server.start(wait = false)

    fun stop() = server.stop(50, 50)

    fun lastCallBody() = calls.lastOrNull() ?: fail("No calls received")

    suspend fun waitForCall(timeout: Duration = 200.milliseconds): String {
        var call = calls.lastOrNull()
        var elapsed = Duration.ZERO

        while (call == null) {
            if (elapsed >= timeout) {
                fail("No calls received")
            }

            val delayDuration = 50.milliseconds
            delay(delayDuration)
            elapsed += delayDuration

            call = calls.lastOrNull()
        }

        return call
    }
}
