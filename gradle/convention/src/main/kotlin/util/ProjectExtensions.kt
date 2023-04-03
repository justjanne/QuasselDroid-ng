package util

import com.android.build.api.dsl.VariantDimension
import org.gradle.api.Incubating
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
import java.util.*

fun Project.cmd(vararg command: String) = try {
  val stdOut = ByteArrayOutputStream()
  exec {
    commandLine(*command)
    standardOutput = stdOut
  }
  stdOut.toString(Charsets.UTF_8.name()).trim()
} catch (e: Throwable) {
  e.printStackTrace()
  null
}

@Suppress("UnstableApiUsage")
@Incubating
inline fun <reified T> VariantDimension.buildConfigField(key: String, value: T) {
  when (value) {
    is String -> this.buildConfigField(
      "String",
      key,
      "\"%s\"".format(value.replace("""\""", """\\""")
        .replace(""""""", """\""""))
    )
    is Long -> this.buildConfigField(
      "long",
      key,
      "%dL".format(value)
    )
    is Int -> this.buildConfigField(
      "int",
      key,
      "%d".format(value)
    )
    is Short -> this.buildConfigField(
      "short",
      key,
      "%d".format(value)
    )
    is Byte -> this.buildConfigField(
      "byte",
      key,
      "%d".format(value)
    )
    is Char -> this.buildConfigField(
      "char",
      key,
      "'%s'".format(value)
    )
    is Double -> this.buildConfigField(
      "double",
      key,
      "%.f".format(value)
    )
    is Float -> this.buildConfigField(
      "float",
      key,
      "%.ff".format(value)
    )
    else -> throw IllegalArgumentException(
      "build config cannot contain values of type " + T::class.java.canonicalName
    )
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
