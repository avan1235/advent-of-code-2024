@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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
    mingwX64(),
  )
  wasmJs {
    browser()
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.procyk.adventofcode.solutions)
    }
  }
}
