@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.lang.Runtime.getRuntime

plugins {
  alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
  jvm()
  listOf(
    macosArm64(),
    macosX64(),
    linuxArm64(),
    linuxX64(),
  )
  wasmJs {
    browser()
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.datetime)
      implementation(libs.kotlinx.io.core)
      implementation(libs.kotlinx.collections.immutable)
      implementation(libs.kotlin.bignum)
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  maxHeapSize = "8g"
  maxParallelForks = getRuntime().availableProcessors().div(2).takeIf { it > 0 } ?: 1
}
