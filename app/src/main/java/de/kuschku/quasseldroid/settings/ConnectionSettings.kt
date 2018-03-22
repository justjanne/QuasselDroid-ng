package de.kuschku.quasseldroid.settings

data class ConnectionSettings(
  val showNotification: Boolean = true
) {
  companion object {
    val DEFAULT = ConnectionSettings()
  }
}