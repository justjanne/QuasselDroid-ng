package de.kuschku.malheur.data

data class CrashInfo(
  val cause: ExceptionInfo?,
  val exception: String?,
  val activeThread: ThreadInfo?,
  val startTime: Long?,
  val crashTime: Long?
)
