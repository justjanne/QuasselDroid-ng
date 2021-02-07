plugins {
  kotlin("jvm")
  id("jacoco")
  id("de.kuschku.coverageconverter")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.getByName<JacocoReport>("jacocoTestReport") {
  reports {
    sourceDirectories.from(fileTree("src/main/kotlin"))
    classDirectories.from(fileTree("build/classes"))
    xml.destination = File("$buildDir/reports/jacoco/report.xml")
    html.isEnabled = true
    xml.isEnabled = true
    csv.isEnabled = false
  }
}

dependencies {
  implementation(kotlin("stdlib"))

  val junit5Version: String by project.extra
  testImplementation("org.junit.jupiter", "junit-jupiter-api", junit5Version)
  testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junit5Version)
  val hamcrestVersion: String by project.extra
  testImplementation("org.hamcrest", "hamcrest-library", hamcrestVersion)
}
