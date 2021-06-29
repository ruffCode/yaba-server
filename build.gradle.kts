import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.5.10"

}

group = "tech.alexib"


java.sourceCompatibility = JavaVersion.VERSION_16
subprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xjsr305=strict", "-Xuse-experimental=kotlin.Experimental",
                "-Xjvm-default=enable",
                "-Xuse-experimental=kotlin.time.ExperimentalTime"
            )
            jvmTarget = "16"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events(
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
            )
        }
    }
}

