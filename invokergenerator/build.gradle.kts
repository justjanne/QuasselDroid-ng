plugins {
  id("justjanne.kotlin")
}

dependencies {
  implementation(libs.ksp)
  implementation(libs.kotlinpoet)
  implementation(project(":invokerannotations"))
}
