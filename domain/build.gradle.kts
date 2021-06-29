plugins{
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.5.0"


}
java.sourceCompatibility = JavaVersion.VERSION_16
val kotestVersion = "4.5.0"
dependencies{
    api("io.arrow-kt:arrow-fx-coroutines:0.13.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation(kotlin("test-junit5"))
    testImplementation("io.mockk:mockk:1.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")

}
