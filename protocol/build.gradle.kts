plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlin("stdlib"))
  api(project(":bitflags"))

  testImplementation("junit", "junit", "4.13.1")
}
