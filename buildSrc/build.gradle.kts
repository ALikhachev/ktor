/*
* Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/

import org.jetbrains.kotlin.utils.addToStdlib.*
import java.util.*

plugins {
    `kotlin-dsl`
}

val buildSnapshotTrain = properties["build_snapshot_train"]?.toString()?.toBoolean() == true

repositories {
    maven("https://plugins.gradle.org/m2")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")

    if (buildSnapshotTrain) {
        mavenLocal()
    }
}

sourceSets.main {
}

configurations.configureEach {
    if (isCanBeResolved) {
        attributes {
            @Suppress("UnstableApiUsage")
            attribute(GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE, project.objects.named(GradleVersion.current().version))
        }
    }
}

dependencies {
    implementation(kotlin("gradle-plugin", libs.versions.kotlin.version.get()))
    implementation(kotlin("serialization", libs.versions.kotlin.version.get()))

    val ktlint_version = libs.versions.ktlint.version.get()
    implementation("org.jmailen.gradle:kotlinter-gradle:$ktlint_version") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-common")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-script-runtime")
    }
}

kotlin {
    jvmToolchain {
        check(this is JavaToolchainSpec)
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xsuppress-version-warnings",
        "-Xskip-metadata-version-check",
    )
}
