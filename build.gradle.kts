import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
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
    implementation(kotlin("reflect"))
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
    kotlinOptions {
        jvmTarget = "${JavaVersion.VERSION_17}"
        javaParameters = true
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
    }
}
