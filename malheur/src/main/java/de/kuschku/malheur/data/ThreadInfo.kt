package de.kuschku.malheur.data

data class ThreadInfo(
  val id: Long?,
  val name: String?,
  val group: String?,
  val status: String?,
  val stackTrace: List<TraceElement>?,
  val isDaemon: Boolean?,
  val priority: Int?
) {
  constructor(thread: Thread, stackTrace: Array<StackTraceElement> = thread.stackTrace) : this(
    id = thread.id,
    name = thread.name,
    group = thread.threadGroup?.name,
    status = thread.state?.name,
    stackTrace = ArrayList(stackTrace.map(::TraceElement)),
    isDaemon = thread.isDaemon,
    priority = thread.priority
  )
}
