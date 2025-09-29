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

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

task<JavaExec>("run") {
    classpath = files(tasks.jar)
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "io.initialcapacity.emailverifier.fakesendgrid.AppKt")
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
