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
  implementation(kotlin("reflect"))
  testImplementation(kotlin("test"))
}

tasks.withType<Test> {
  minHeapSize = "1g"
  maxHeapSize = "2g"
}
