import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  kotlin("kapt")
}

val appCompatVersion = "26.1.0"
dependencies {
  implementation(kotlin("stdlib", "1.1.51"))

  implementation(appCompat("support-annotations"))
  implementation("org.threeten:threetenbp:1.3.6")
  implementation("io.reactivex.rxjava2:rxjava:2.1.3")

  implementation(project(":invokerannotations"))
  kapt(project(":invokergenerator"))

  testImplementation("junit:junit:4.12")
}

tasks.withType(KotlinCompile::class.java) {
  kotlinOptions {
    freeCompilerArgs = listOf(
      "-Xno-param-assertions",
      "-Xno-call-assertions"
    )
  }
}

/**
 * Builds the dependency notation for the named AppCompat [module] at the given [version].
 *
 * @param module simple name of the AppCompat module, for example "cardview-v7".
 * @param version optional desired version, null implies [appCompatVersion].
 */
fun appCompat(module: String, version: String? = null)
  = "com.android.support:$module:${version ?: appCompatVersion}"
