plugins {
  kotlin("jvm")
  id("jacoco")
  id("de.justjanne.jacoco-cobertura-converter")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

coverage {
  autoConfigureCoverage = true
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.2")
  implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-test", "1.4.2")
  api(project(":protocol"))

  val junit5Version: String by project
  testImplementation("org.junit.jupiter", "junit-jupiter-api", junit5Version)
  testImplementation("org.junit.jupiter", "junit-jupiter-params", junit5Version)
  testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junit5Version)
  val hamcrestVersion: String by project
  testImplementation("org.hamcrest", "hamcrest-library", hamcrestVersion)

  val testcontainersCiVersion: String by project
  testImplementation("de.justjanne", "testcontainers-ci", testcontainersCiVersion)
  val sl4jVersion: String by project
  testImplementation("org.slf4j", "slf4j-simple", sl4jVersion)
}
