import org.gradle.api.file.DuplicatesStrategy.INCLUDE

plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project
val exposedVersion: String by project
val hikariVersion: String by project
val postgresVersion: String by project
val logbackVersion: String by project
val rabbitVersion: String by project

dependencies {
    implementation(project(":components:registration-request"))
    implementation(project(":components:registration"))
    implementation(project(":components:database-support"))
    implementation(project(":components:rabbit-support"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    testImplementation(project(":components:test-support"))
    testImplementation(project(":components:test-database-support"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-java:$ktorVersion")
}

task<JavaExec>("run") {
    classpath = files(tasks.jar)
    environment("DATABASE_URL", "jdbc:postgresql://localhost:5555/registration_dev?user=emailverifier&password=emailverifier")
    environment("RABBIT_URL", "amqp://localhost:5672")
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "io.initialcapacity.emailverifier.registrationserver.AppKt")
        }

        duplicatesStrategy = INCLUDE

        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map {
                    zipTree(it)
                }
        })
    }
}
