plugins {
  kotlin("jvm")
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("org.threeten", "threetenbp", "1.4.0")
  api(project(":bitflags"))

  testImplementation("junit", "junit", "4.13.1")
}
