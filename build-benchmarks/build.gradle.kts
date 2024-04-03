/*
 * Copyright 2014-2022 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

group = "org.jetbrains"
version = "1.0-SNAPSHOT"

plugins {
    application
    kotlin("jvm") version "1.9.23"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
}

dependencies {
    implementation("org.jetbrains:kotlin-build-benchmarks:1.0-SNAPSHOT")
}

application {
    mainClassName = "RunBenchmarksKt"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xskip-prerelease-check",
            "-opt-in=kotlin.ExperimentalUnsignedTypes"
        )
    }
}
