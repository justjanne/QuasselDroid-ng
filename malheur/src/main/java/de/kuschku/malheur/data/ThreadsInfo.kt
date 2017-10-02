package de.kuschku.malheur.data

data class ThreadsInfo(
  val crashed: ThreadInfo?,
  val others: List<ThreadInfo>?
)
