import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("justjanne.java")
  id("justjanne.repositories")
  id("com.google.devtools.ksp")
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf(
      "-opt-in=kotlin.ExperimentalUnsignedTypes",
      "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
      "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
    )
    jvmTarget = "1.8"
  }
}
