plugins {
    id("email-verifier.java-conventions")
}

val rabbitVersion: String by project

dependencies {
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    testImplementation(project(":components:test-support"))
}
