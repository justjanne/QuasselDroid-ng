package de.kuschku.malheur.config

data class DeviceConfig(
  val build: Boolean = true,
  val version: Boolean = true,
  val installationId: Boolean = true,
  val processor: Boolean = true,
  val runtime: Boolean = true,
  val display: Boolean = true
)
