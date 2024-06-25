package io.initialcapacity.emailverifier.notificationserver

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

class DatabaseConfiguration(private val dbUrl: String) {
    private val config = HikariConfig().apply { jdbcUrl = dbUrl }
    private val ds = HikariDataSource(config)

    val db by lazy {
        Database.connect(ds)
    }
}
