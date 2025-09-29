package io.initialcapacity.emailverifier.databasesupport

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

class DatabaseTemplate(private val db: Database) {
    fun execute(sql: String, vararg arguments: Any): Unit =
        transaction(db) {
            connection.prepareStatement(sql, false)
                .also {
                    for ((index, argument) in arguments.withIndex()) {
                        it[index + 1] = argument
                    }
                }.executeUpdate()
        }

    fun <T> queryOne(sql: String, vararg arguments: Any, mapping: (ResultSet) -> T): T? =
        transaction(db) {
            val resultSet = connection.prepareStatement(sql, false).also {
                for ((index, argument) in arguments.withIndex()) {
                    it[index + 1] = argument
                }
            }.executeQuery()

            if (!resultSet.next()) {
                return@transaction null
            }

            return@transaction mapping(resultSet)
        }
}
