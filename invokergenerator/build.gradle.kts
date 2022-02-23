plugins {
  id("justjanne.kotlin")
}

repositories {
  google()
}

dependencies {
  implementation(libs.ksp)
  implementation(libs.kotlinpoet)
  implementation(project(":invokerannotations"))
}
