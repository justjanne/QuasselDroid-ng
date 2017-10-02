package de.kuschku.malheur.config

data class EnvConfig(
  val paths: Boolean = true,
  val memory: Boolean = true,
  val configuration: Boolean = true,
  val startTime: Boolean = true,
  val crashTime: Boolean = true
)
