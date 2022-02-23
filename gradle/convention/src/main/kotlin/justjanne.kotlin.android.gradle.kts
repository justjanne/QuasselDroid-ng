import gradle.kotlin.dsl.accessors._5ee02a09f3b6dcc9700f8e51e448c252.implementation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("justjanne.repositories")
  id("com.google.devtools.ksp")
  kotlin("android")
  kotlin("kapt")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
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
