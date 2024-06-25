import org.gradle.api.file.DuplicatesStrategy.INCLUDE

plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project
val logbackVersion: String by project

dependencies {
    implementation(project(":components:fake-sendgrid-endpoints"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-java:$ktorVersion")

    implementation("io.dropwizard.metrics:metrics-core:4.2.0")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

task<JavaExec>("run") {
    classpath = files(tasks.jar)
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "io.initialcapacity.emailverifier.benchmark.AppKt")
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
