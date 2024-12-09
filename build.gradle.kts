import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "wtf.cwrau"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("wtf.cwrau.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.0.2")
    implementation("io.ktor:ktor-client-cio:3.0.2")
    implementation("io.ktor:ktor-client-cio-jvm:3.0.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.8.10")
    testRuntimeOnly("org.junit.platform", "junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    testLogging {
        info.events = info.events union events
    }
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        javaParameters = true
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
    }
}
