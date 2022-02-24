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
