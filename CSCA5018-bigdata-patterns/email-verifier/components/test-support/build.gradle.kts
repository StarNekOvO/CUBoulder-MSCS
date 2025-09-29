plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project
val rabbitVersion: String by project

dependencies {
    api("io.ktor:ktor-server-core:$ktorVersion")
    api("io.ktor:ktor-server-jetty:$ktorVersion")
    api("io.ktor:ktor-server-double-receive:$ktorVersion")

    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")
    implementation(kotlin("test-junit"))

    implementation(project(":components:rabbit-support"))
}