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

tasks.test {
  useJUnitPlatform()
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
  implementation(kotlin("reflect"))

  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
  testImplementation(kotlin("test"))
}

tasks.withType<Test> {
  minHeapSize = "16g"
  maxHeapSize = "16g"
}
