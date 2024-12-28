@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

import java.lang.Runtime.getRuntime

plugins {
  alias(libs.plugins.kotlinMultiplatform)
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
    mingwX64(),
  ).forEach { target ->
    target.binaries.executable {
      entryPoint = "main"
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(project(":solutions"))

      implementation(libs.procyk.adventofcode.runner)
    }

    commonTest.dependencies {
      implementation(project(":solutions"))

      implementation(libs.procyk.adventofcode.test.runner)
      implementation(libs.kotlin.test)
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  maxHeapSize = "8g"
  maxParallelForks = getRuntime().availableProcessors().div(2).takeIf { it > 0 } ?: 1
}
