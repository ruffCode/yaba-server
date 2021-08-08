import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    id("org.springframework.boot") version "2.5.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm")
    kotlin("plugin.spring") version "1.5.10"
    kotlin("kapt")
//    id("org.springframework.experimental.aot") version "0.10.0-SNAPSHOT"
    id("com.expediagroup.graphql") version "4.1.1"
    kotlin("plugin.serialization") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"


}

group = "tech.alexib"


java.sourceCompatibility = JavaVersion.VERSION_16

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

extra["testcontainersVersion"] = "1.16.0"
val kotestVersion = "4.6.1"
dependencies {
    implementation(project(":domain"))
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux") {
        exclude(module = "spring-boot-starter-json")
    }
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.expediagroup:graphql-kotlin-spring-server:5.0.0-alpha.3")
    implementation("com.expediagroup:graphql-kotlin-hooks-provider:5.0.0-alpha.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
    implementation("io.github.microutils:kotlin-logging:2.0.10")
    implementation("io.r2dbc:r2dbc-pool")
    implementation("io.r2dbc:r2dbc-postgresql")
    implementation("org.postgresql:postgresql")
    implementation("com.auth0:java-jwt:3.14.0")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("tech.alexib:plaid-kotlin:0.0.21")
    implementation("io.sentry:sentry-spring-boot-starter:5.0.1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.google.firebase:firebase-admin:7.3.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions-spring:4.4.3")
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("app.cash.turbine:turbine:0.6.0")

}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<BootBuildImage> {
    val dockerImageName:String by project
    imageName = "yaba-server:$version"
//    builder = "paketobuildpacks/builder:tiny"
    builder = "paketobuildpacks/builder:base"
//    environment = mapOf("BP_NATIVE_IMAGE" to "true")
}
graphql {
    schema {
        packages = listOf("tech.alexib.yaba.server", "tech.alexib.yaba.domain")
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("yaba-server")
}
