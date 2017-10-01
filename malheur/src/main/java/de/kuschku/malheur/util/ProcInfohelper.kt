package de.kuschku.malheur.util

import java.io.File

fun readProcInfo() = File("/proc/cpuinfo")
  .bufferedReader(Charsets.UTF_8)
  .lineSequence()
  .map { line -> line.split(":") }
  .filter { split -> split.size == 2 }
  .map { (key, value) -> key.trim() to value.trim() }
  .filter { (key, _) -> key == "Hardware" }
  .map { (_, value) -> value }
  .firstOrNull()
