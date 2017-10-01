package de.kuschku.malheur.data

data class AppInfo(
  val versionName: String?,
  val versionCode: Int?,
  val buildConfig: Map<String, Any?>?
)
