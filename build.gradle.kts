import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    application
    jacoco
    id("org.sonarqube") version "4.4.0.3356"
}
group = "org.izaguirre"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "19"
}
tasks.test {
    useJUnitPlatform()
}

application.mainClass.set("org.izaguirre.chip8.swing.MainKt")

jacoco {
    toolVersion = "0.8.10"
}
tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}