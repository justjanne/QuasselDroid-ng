package de.kuschku.justcode

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class JustCodePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.run {
      this.extensions.getByType<BaseAppModuleExtension>().run {
        setCompileSdkVersion(30)
        buildToolsVersion = "30.0.3"

        signingConfigs {
          signingData(project.rootProject.properties("signing.properties"))?.let {
            create("default") {
              storeFile = file(it.storeFile)
              storePassword = it.storePassword
              keyAlias = it.keyAlias
              keyPassword = it.keyPassword
            }
          }
        }

        defaultConfig {
          versionCode = gitVersionCode() ?: 1
          versionName = gitVersionName() ?: "1.0.0"

          setBuildConfigField(
            ::buildConfigField,
            "GIT_HEAD",
            gitHead() ?: ""
          )
          setBuildConfigField(
            ::buildConfigField,
            "FANCY_VERSION_NAME",
            fancyVersionName() ?: "1.0.0"
          )
          setBuildConfigField(
            ::buildConfigField,
            "GIT_COMMIT_DATE",
            gitCommitDate()
          )

          signingConfig = signingConfigs.findByName("default")
        }
      }
    }
  }
}
