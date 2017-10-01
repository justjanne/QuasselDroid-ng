package de.kuschku.malheur.config

data class CrashConfig(
  val cause: Boolean = true,
  val exception: Boolean = true,
  val activeThread: Boolean = true,
  val startTime: Boolean = true,
  val crashTime: Boolean = true,
  val configuration: Boolean = true
)
