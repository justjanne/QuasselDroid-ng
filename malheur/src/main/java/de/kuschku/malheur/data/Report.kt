package de.kuschku.malheur.data

data class Report(
  val crash: CrashInfo?,
  val threads: ThreadsInfo?,
  val logcat: Map<String, List<String>?>?,
  val application: AppInfo?,
  val device: DeviceInfo?,
  val environment: EnvInfo?
)
