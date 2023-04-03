import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

@Suppress("UnstableApiUsage")
class KotlinConvention : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("org.jetbrains.kotlin.jvm")
        apply("org.jetbrains.kotlin.kapt")
        apply("com.google.devtools.ksp")
      }

      extensions.configure<KotlinJvmOptions> {
        freeCompilerArgs = freeCompilerArgs + listOf(
          "-opt-in=kotlin.RequiresOptIn",
          "-opt-in=kotlin.ExperimentalUnsignedTypes",
          "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
          "-opt-in=kotlinx.coroutines.FlowPreview",
        )

        jvmTarget = JavaVersion.VERSION_11.toString()
      }

      tasks.withType<Test> {
        useJUnitPlatform()
      }

      configure<JavaPluginExtension> {
        toolchain {
          languageVersion.set(JavaLanguageVersion.of(11))
        }
      }
    }
  }
}
