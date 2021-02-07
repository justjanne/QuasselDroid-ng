plugins {
  kotlin("jvm")
  jacoco
  id("de.kuschku.coverageconverter")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

jacoco {
  toolVersion = "0.8.3"
}

tasks.getByName<JacocoReport>("jacocoTestReport") {
  reports {
    sourceDirectories.from(fileTree("src/main/kotlin"))
    xml.destination = File("$buildDir/reports/jacoco/report.xml")
    html.isEnabled = true
    xml.isEnabled = true
    csv.isEnabled = false
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("org.threeten", "threetenbp", "1.4.0")
  api(project(":bitflags"))
  api(project(":coverage-annotations"))

  val junit5Version: String by project.extra
  testImplementation("org.junit.jupiter", "junit-jupiter-api", junit5Version)
  testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junit5Version)
  val hamcrestVersion: String by project.extra
  testImplementation("org.hamcrest", "hamcrest-library", hamcrestVersion)
}
