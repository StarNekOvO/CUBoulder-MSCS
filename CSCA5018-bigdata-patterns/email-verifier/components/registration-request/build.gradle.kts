plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project
val rabbitVersion: String by project

dependencies {
    implementation(project(":components:database-support"))
    implementation(project(":components:rabbit-support"))
    implementation(project(":components:serialization-support"))

    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    testImplementation(project(":components:test-support"))
    testImplementation(project(":components:test-database-support"))
}
