package de.kuschku.malheur.data

data class Report(
  val crash: CrashInfo? = null,
  val threads: ThreadsInfo? = null,
  val logcat: Map<String, List<String>?>? = null,
  val application: AppInfo? = null,
  val device: DeviceInfo? = null,
  val environment: EnvInfo? = null
)
