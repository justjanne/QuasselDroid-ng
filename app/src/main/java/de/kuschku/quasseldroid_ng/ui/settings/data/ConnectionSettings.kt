package de.kuschku.quasseldroid_ng.ui.settings.data

data class ConnectionSettings(
  val showNotification: Boolean = true
) {
  companion object {
    val DEFAULT = ConnectionSettings()
  }
}