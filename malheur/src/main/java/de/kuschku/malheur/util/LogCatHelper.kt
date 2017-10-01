package de.kuschku.malheur.util

fun readLogCat(since: String, buffer: String, pid: String) = ProcessBuilder()
  .command("logcat", "-t", since, "-b", buffer, "--pid", pid)
  .redirectErrorStream(true)
  .start()
  .inputStream
  .bufferedReader(Charsets.UTF_8)
  .readLines()
