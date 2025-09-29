package io.milk.testsupport

import io.milk.database.createDatasource
import javax.sql.DataSource

const val testJdbcUrl = "jdbc:postgresql://localhost:5432/milk_test"
const val testDbUsername = "milk"
const val testDbPassword = "milk"

fun testDataSource(): DataSource {
    return createDatasource(
        jdbcUrl = testJdbcUrl,
        username = testDbUsername,
        password = testDbPassword
    )
}
