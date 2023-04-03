import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import util.kotlinOptions

@Suppress("UnstableApiUsage")
class KotlinAndroidConvention : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("org.jetbrains.kotlin.android")
        apply("org.jetbrains.kotlin.kapt")
        apply("com.google.devtools.ksp")
      }

      extensions.configure<BaseExtension> {
        kotlinOptions {
          freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlin.ExperimentalUnsignedTypes",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.paging.ExperimentalPagingApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
          )

          jvmTarget = JavaVersion.VERSION_11.toString()
        }
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
