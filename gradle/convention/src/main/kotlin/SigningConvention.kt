import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import util.properties

class SigningConvention : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      with(pluginManager) {
        apply("com.android.application")
        apply("justjanne.kotlin.android")
      }

      extensions.configure<ApplicationExtension> {
        signingConfigs {
          SigningData.of(rootProject.properties("signing.properties"))?.let {
            create("default") {
              storeFile = file(it.storeFile)
              storePassword = it.storePassword
              keyAlias = it.keyAlias
              keyPassword = it.keyPassword
            }
          }
        }
      }
    }
  }
}
