package de.kuschku.malheur.data

data class CrashInfo(
  val cause: ExceptionInfo?,
  val exception: String?,
  val activeThread: ThreadInfo?,
  val startTime: String?,
  val crashTime: String?,
  val configuration: Map<String, Any?>?
)
