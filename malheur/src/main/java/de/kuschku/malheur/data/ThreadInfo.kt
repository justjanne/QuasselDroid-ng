package de.kuschku.malheur.data

data class ThreadInfo(
  val id: Long?,
  val name: String?,
  val group: String?,
  val status: String?,
  val stackTrace: List<TraceElement>?,
  val isDaemon: Boolean?,
  val priority: Int?
)
