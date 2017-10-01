package de.kuschku.malheur.config

data class LogConfig(
  val buffers: List<String> = listOf("main", "events", "crash")
)
