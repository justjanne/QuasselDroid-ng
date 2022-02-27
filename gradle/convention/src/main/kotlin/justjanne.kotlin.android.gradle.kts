import gradle.kotlin.dsl.accessors._9f9f63157b527b37420ecbe9e569524a.implementation
import gradle.kotlin.dsl.accessors._9f9f63157b527b37420ecbe9e569524a.testImplementation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("justjanne.repositories")
  id("com.google.devtools.ksp")
  kotlin("android")
  kotlin("kapt")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
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
