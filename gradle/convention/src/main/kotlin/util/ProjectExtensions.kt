package util

import org.gradle.api.Project
import java.util.Properties

@Suppress("UnstableApiUsage")
fun Project.git(vararg command: String): String? = try {
  providers.exec {
    commandLine("git", *command)
  }.standardOutput.asText.get().trim()
} catch (t: Throwable) {
  null
}

fun Project.properties(fileName: String): Properties? {
  val file = file(fileName)
  if (!file.exists())
    return null
  val props = Properties()
  props.load(file.inputStream())
  return props
}
