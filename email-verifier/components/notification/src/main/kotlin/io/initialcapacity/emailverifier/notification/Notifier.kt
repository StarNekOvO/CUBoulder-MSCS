package io.initialcapacity.emailverifier.notification

import org.slf4j.LoggerFactory
import java.util.*

class Notifier(
    private val gateway: NotificationDataGateway,
    private val emailer: Emailer
) {
    private val logger = LoggerFactory.getLogger(Notifier::class.java)

    suspend fun notify(email: String, confirmationCode: UUID) {
        gateway.save(email, confirmationCode)
        val subject = "Confirmation code"
        val message = "Your confirmation code is $confirmationCode"

        logger.debug("sending notification to: {}, subject: {}, message: {}", email, subject, message)
        emailer.send(
            toAddress = email,
            subject = subject,
            message = message
        )
    }
}
