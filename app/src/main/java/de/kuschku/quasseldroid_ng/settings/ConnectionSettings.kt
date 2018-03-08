package de.kuschku.quasseldroid_ng.settings

data class ConnectionSettings(
  val showNotification: Boolean = true
) {
  companion object {
    val DEFAULT = ConnectionSettings()
  }
}