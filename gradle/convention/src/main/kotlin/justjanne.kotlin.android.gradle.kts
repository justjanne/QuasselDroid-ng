import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("justjanne.repositories")
  id("com.google.devtools.ksp")
  kotlin("android")
  kotlin("kapt")
}

dependencies {
  "implementation"("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
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
