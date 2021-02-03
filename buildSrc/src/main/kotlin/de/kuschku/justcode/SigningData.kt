package de.kuschku.justcode

import java.util.*

data class SigningData(
  val storeFile: String,
  val storePassword: String,
  val keyAlias: String,
  val keyPassword: String
)

fun signingData(properties: Properties?): SigningData? {
  if (properties == null) return null

  val storeFile = properties.getProperty("storeFile") ?: return null
  val storePassword = properties.getProperty("storePassword") ?: return null
  val keyAlias = properties.getProperty("keyAlias") ?: return null
  val keyPassword = properties.getProperty("keyPassword") ?: return null

  return SigningData(storeFile, storePassword, keyAlias, keyPassword)
}
