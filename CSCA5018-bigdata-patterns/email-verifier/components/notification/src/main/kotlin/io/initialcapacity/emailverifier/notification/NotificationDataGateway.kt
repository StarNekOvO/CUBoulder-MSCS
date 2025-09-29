package io.initialcapacity.emailverifier.notification

import io.initialcapacity.emailverifier.databasesupport.DatabaseTemplate
import java.util.*

class NotificationDataGateway(private val dbTemplate: DatabaseTemplate) {
    fun find(email: String): UUID? = dbTemplate.queryOne(
        //language=SQL
        "select confirmation_code from notifications where email = ?",
        email
    ) { it.getObject("confirmation_code", UUID::class.java) }

    fun save(email: String, confirmationCode: UUID) = dbTemplate.execute(
        //language=SQL
        "insert into notifications (email, confirmation_code) values (?, ?)",
        email, confirmationCode,
    )
}
