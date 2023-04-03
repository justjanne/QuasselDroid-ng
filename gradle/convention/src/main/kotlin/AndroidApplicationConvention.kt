import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import util.buildConfigField
import util.cmd
import util.properties
import java.util.*

@Suppress("UnstableApiUsage")
class AndroidApplicationConvention : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("justjanne.kotlin.android")
      }

      extensions.configure<ApplicationExtension> {
        compileSdk = 33

        defaultConfig {
          minSdk = 21
          targetSdk = 33

          applicationId = "${rootProject.group}.${rootProject.name.lowercase(Locale.ROOT)}"
          versionCode = cmd("git", "rev-list", "--count", "HEAD")?.toIntOrNull() ?: 1
          versionName = cmd("git", "describe", "--always", "--tags", "HEAD") ?: "1.0.0"

          buildConfigField("GIT_HEAD",
            cmd("git", "rev-parse", "HEAD") ?: "")
          buildConfigField("GIT_COMMIT_DATE",
            cmd("git", "show", "-s", "--format=%ct")?.toLongOrNull() ?: 0L)

          signingConfig = signingConfigs.findByName("default")

          setProperty("archivesBaseName", "${rootProject.name}-$versionName")

          // Disable test runner analytics
          testInstrumentationRunnerArguments["disableAnalytics"] = "true"
        }

        signingConfigs {
          SigningData.of(project.rootProject.properties("signing.properties"))?.let {
            create("default") {
              storeFile = file(it.storeFile)
              storePassword = it.storePassword
              keyAlias = it.keyAlias
              keyPassword = it.keyPassword
            }
          }
        }

        compileOptions {
          sourceCompatibility = JavaVersion.VERSION_11
          targetCompatibility = JavaVersion.VERSION_11
        }

        testOptions {
          unitTests.isIncludeAndroidResources = true
        }

        lint {
          warningsAsErrors = true
          lintConfig = file("../lint.xml")
        }
      }
    }
  }
}
