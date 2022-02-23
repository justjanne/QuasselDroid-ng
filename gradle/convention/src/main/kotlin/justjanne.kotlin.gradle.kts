import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("justjanne.java")
  id("justjanne.repositories")
  id("com.google.devtools.ksp")
  kotlin("jvm")
  kotlin("kapt")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf(
      "-Xinline-classes",
      "-Xopt-in=kotlin.ExperimentalUnsignedTypes"
    )
    jvmTarget = "1.8"
  }
}
