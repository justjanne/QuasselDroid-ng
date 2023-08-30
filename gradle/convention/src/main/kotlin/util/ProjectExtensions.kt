package util

import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.util.Properties

fun Project.git(vararg command: String) = try {
  val stdOut = ByteArrayOutputStream()
  exec {
    commandLine("git", *command)
    standardOutput = stdOut
  }
  stdOut.toString(Charsets.UTF_8.name()).trim()
} catch (e: Throwable) {
  e.printStackTrace()
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
