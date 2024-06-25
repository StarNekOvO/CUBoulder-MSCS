package io.milk.database

import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

fun createDatasource(
    jdbcUrl: String, username: String, password: String,
): DataSource = HikariDataSource().apply {
    maximumPoolSize = 2
    setJdbcUrl(jdbcUrl)
    setUsername(username)
    setPassword(password)
}
