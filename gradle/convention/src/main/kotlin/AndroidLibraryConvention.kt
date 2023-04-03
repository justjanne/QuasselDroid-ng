import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.*

@Suppress("UnstableApiUsage")
class AndroidLibraryConvention : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.library")
        apply("justjanne.kotlin.android")
      }

      extensions.configure<LibraryExtension> {
        compileSdk = 33

        defaultConfig {
          minSdk = 21

          consumerProguardFiles("proguard-rules.pro")

          // Disable test runner analytics
          testInstrumentationRunnerArguments["disableAnalytics"] = "true"
        }

        lint {
          warningsAsErrors = true
          lintConfig = file("../lint.xml")
        }
      }
    }
  }
}
