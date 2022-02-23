import java.io.ByteArrayOutputStream
import java.util.*

plugins {
  id("com.android.application")
}

android {
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

  defaultConfig {
    signingConfig = signingConfigs.findByName("default")
  }
}

fun Project.properties(fileName: String): Properties? {
  val file = file(fileName)
  if (!file.exists())
    return null
  val props = Properties()
  props.load(file.inputStream())
  return props
}

data class SigningData(
  val storeFile: String,
  val storePassword: String,
  val keyAlias: String,
  val keyPassword: String
) {
  companion object {
    fun of(properties: Properties?): SigningData? {
      if (properties == null) return null

      val storeFile = properties.getProperty("storeFile") ?: return null
      val storePassword = properties.getProperty("storePassword") ?: return null
      val keyAlias = properties.getProperty("keyAlias") ?: return null
      val keyPassword = properties.getProperty("keyPassword") ?: return null

      return SigningData(storeFile, storePassword, keyAlias, keyPassword)
    }
  }
}
