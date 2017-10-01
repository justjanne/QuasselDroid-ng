package de.kuschku.malheur.data

data class DeviceInfo(
  val build: Map<String, Any?>?,
  val version: Map<String, Any?>?,
  val installationId: String?,
  val processor: String?
)
