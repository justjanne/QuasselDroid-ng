package de.kuschku.malheur.data

data class EnvInfo(
  val paths: Map<String, Any?>?,
  val memory: MemoryInfo?,
  val configuration: Map<String, Any?>?,
  val startTime: Long?,
  val crashTime: Long?
)
