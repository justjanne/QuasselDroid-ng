plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlin("stdlib"))

  val testContainersVersion: String by project.extra
  api("org.testcontainers", "testcontainers", testContainersVersion)
  api("org.testcontainers", "junit-jupiter", testContainersVersion)
}
