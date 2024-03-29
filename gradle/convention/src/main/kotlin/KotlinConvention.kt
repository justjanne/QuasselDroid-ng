import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.tasks.KaptGenerateStubs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

class KotlinConvention : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("org.jetbrains.kotlin.jvm")
        apply("org.jetbrains.kotlin.kapt")
        apply("com.google.devtools.ksp")
        apply("org.jetbrains.kotlin.plugin.serialization")
      }

      // Use withType to workaround https://youtrack.jetbrains.com/issue/KT-55947
      tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
          // Set JVM target to 11
          jvmTarget = JavaVersion.VERSION_11.toString()
          // Treat all Kotlin warnings as errors (disabled by default)
          // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
          val warningsAsErrors: String? by target
          allWarningsAsErrors = warningsAsErrors.toBoolean()
          freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.ExperimentalUnsignedTypes"
          )
        }
      }

      tasks.withType<Test> {
        useJUnitPlatform()
      }

      configure<JavaPluginExtension> {
        // Up to Java 11 APIs are available through desugaring
        // https://developer.android.com/studio/write/java11-minimal-support-table
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        toolchain {
          languageVersion.set(JavaLanguageVersion.of(17))
        }
      }
    }
  }
}
