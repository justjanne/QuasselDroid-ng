import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import util.git
import java.util.Locale

class AndroidApplicationConvention : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("justjanne.kotlin.android")
        apply("justjanne.signing")
      }

      extensions.configure<ApplicationExtension> {
        compileSdk = 34

        defaultConfig {
          minSdk = 21
          targetSdk = 34

          applicationId = "${rootProject.group}.${rootProject.name.lowercase(Locale.ROOT)}"

          val commit = git("rev-parse", "HEAD")!!
          val name = git("describe", "--always", "--tags", "HEAD")!!

          versionCode = git("rev-list", "--count", "HEAD")!!.toInt()
          versionName = git("describe", "--always", "--tags", "HEAD")!!

          val fancyVersionName = "<a href=\\\"https://git.kuschku.de/justJanne/QuasselDroid-ng/commit/$commit\\\">$name</a>"

          buildConfigField("String", "GIT_HEAD", "\"${git("rev-parse", "HEAD") ?: ""}\"")
          buildConfigField("String", "FANCY_VERSION_NAME", "\"${fancyVersionName ?: ""}\"")
          buildConfigField("long", "GIT_COMMIT_DATE", "${git("show", "-s", "--format=%ct") ?: 0}L")

          signingConfig = signingConfigs.findByName("default")

          setProperty("archivesBaseName", "${rootProject.name}-$versionName")

          // Disable test runner analytics
          testInstrumentationRunnerArguments["disableAnalytics"] = "true"
        }

        buildFeatures {
          buildConfig = true
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
