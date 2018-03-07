import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  implementation(kotlin("stdlib", "1.2.30"))

  withVersion("27.1.0") {
    implementation("com.android.support", "support-annotations", version)
  }

  implementation("org.threeten", "threetenbp", "1.3.6")
  implementation("io.reactivex.rxjava2:rxjava:2.1.9")

  implementation(project(":invokerannotations"))
  kapt(project(":invokergenerator"))

  testImplementation("junit", "junit", "4.12")
}