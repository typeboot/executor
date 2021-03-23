plugins {
    id("org.sonarqube") version "2.8"
    id("jacoco")
    kotlin("jvm") version "1.3.71"
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":jdbc"))
    implementation("com.fasterxml.jackson.core:jackson-core:2.11.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.3")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.3.7")

    implementation("com.datastax.oss:java-driver-core:4.10.0")
    implementation("org.slf4j:slf4j-api:1.7.26")
}