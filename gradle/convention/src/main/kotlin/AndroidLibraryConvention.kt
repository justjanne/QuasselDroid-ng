import org.gradle.api.JavaVersion
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.*

class AndroidLibraryConvention : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.library")
        apply("justjanne.kotlin.android")
      }

      extensions.configure<LibraryExtension> {
        compileSdk = 34

        defaultConfig {
          minSdk = 21

          consumerProguardFiles("proguard-rules.pro")

          // Disable test runner analytics
          testInstrumentationRunnerArguments["disableAnalytics"] = "true"
        }

        compileOptions {
          sourceCompatibility = JavaVersion.VERSION_11
          targetCompatibility = JavaVersion.VERSION_11
        }

        lint {
          warningsAsErrors = true
          lintConfig = file("../lint.xml")
        }
      }
    }
  }
}
