plugins {
  id("justjanne.kotlin")
}

repositories {
  google()
}

dependencies {
  implementation("com.google.devtools.ksp:symbol-processing-api:1.6.10-1.0.4")
  implementation(project(":invokerannotations"))
  implementation("com.squareup", "kotlinpoet", "1.8.0")
}
