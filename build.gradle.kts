import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.5.10"
    id("com.gorylenko.gradle-git-properties") version "2.2.4"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("org.jlleitschuh.gradle.ktlint-idea").version("10.1.0")
    id("com.diffplug.spotless") version "5.14.2"
    id("io.gitlab.arturbosch.detekt") version "1.18.0-RC3"
}

group = "tech.alexib"

java.sourceCompatibility = JavaVersion.VERSION_16
subprojects {
    apply<io.gitlab.arturbosch.detekt.DetektPlugin>()
    apply<org.jlleitschuh.gradle.ktlint.KtlintPlugin>()
    apply<com.diffplug.gradle.spotless.SpotlessPlugin>()

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
//            showStandardStreams = true
//            showExceptions= true
            events(
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
            )
        }
    }

    ktlint {
        debug.set(true)
        version.set("0.42.1")
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(true)
        enableExperimentalRules.set(true)
        filter {
            exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }
            include("**/kotlin/**")
        }
        outputToConsole.set(true)
        outputColorName.set("BLUE")
        ignoreFailures.set(true)

        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
        additionalEditorconfigFile.set(file("${project.projectDir}/.editorConfig"))
    }
    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension>() {
        config = rootProject.files("config/detekt/detekt.yml")
        reports {
            html {
                enabled = true
                destination = file("build/reports/detekt.html")
            }
            autoCorrect = true
            parallel = true
        }
    }
    spotless {
        isEnforceCheck = false
        kotlin {
            target("src/**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("**/generated/**")
            targetExclude("spotless/copyright.kt")
            licenseHeaderFile {
                rootProject.file("spotless/copyright.kt")
            }
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint("0.42.1")
        }
    }
}
