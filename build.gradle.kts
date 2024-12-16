import java.lang.Runtime.getRuntime

plugins {
  kotlin("jvm") version "2.1.0"
  application
}

group = "in.procyk"
version = "1.0"

repositories {
  mavenCentral()
}

application {
  mainClass.set("AdventKt")
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
  implementation(kotlin("reflect"))

  testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
  maxHeapSize = "8g"
  maxParallelForks = getRuntime().availableProcessors().div(2).takeIf { it > 0 } ?: 1
}
