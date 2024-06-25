package io.initialcapacity.emailverifier.registrationrequest

import io.initialcapacity.emailverifier.databasesupport.DatabaseTemplate
import java.util.*

class RegistrationRequestDataGateway(private val dbTemplate: DatabaseTemplate) {
    fun find(email: String): UUID? = dbTemplate.queryOne(
        //language=SQL
        "select confirmation_code from registration_requests where email = ?",
        email
    ) { it.getObject("confirmation_code", UUID::class.java) }

    fun save(email: String, confirmationCode: UUID) = dbTemplate.execute(
        //language=SQL
        "insert into registration_requests (email, confirmation_code) values (?, ?)",
        email, confirmationCode
    )
}
