package de.kuschku.malheur.util

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.printStackTraceToString(): String? {
  val result = StringWriter()
  val printWriter = PrintWriter(result)
  printStackTrace(printWriter)
  return result.toString()
}
