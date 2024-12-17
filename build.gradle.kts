@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import java.lang.Runtime.getRuntime

plugins {
  kotlin("multiplatform") version "2.1.0"
}

group = "in.procyk"
version = "1.0"

repositories {
  mavenCentral()
}

kotlin {
  jvm {
    mainRun {
      mainClass = "AdventKt"
    }
  }
  listOf(
    macosArm64(),
    macosX64(),
    linuxArm64(),
    linuxX64(),
  ).forEach { target ->
    target.binaries.executable {
      entryPoint = "main"
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
      implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
      implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
      implementation("com.ionspin.kotlin:bignum:0.3.10")
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  maxHeapSize = "8g"
  maxParallelForks = getRuntime().availableProcessors().div(2).takeIf { it > 0 } ?: 1
}
