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
  api("org.threeten", "threetenbp", "1.4.0")
  val kotlinBitflagsVersion: String by project
  api("de.justjanne", "kotlin-bitflags", kotlinBitflagsVersion)
  api(project(":coverage-annotations"))

  val junit5Version: String by project
  testImplementation("org.junit.jupiter", "junit-jupiter-api", junit5Version)
  testImplementation("org.junit.jupiter", "junit-jupiter-params", junit5Version)
  testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junit5Version)
  val hamcrestVersion: String by project
  testImplementation("org.hamcrest", "hamcrest-library", hamcrestVersion)
}
