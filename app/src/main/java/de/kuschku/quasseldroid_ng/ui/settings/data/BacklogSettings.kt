package de.kuschku.quasseldroid_ng.ui.settings.data

data class BacklogSettings(
  val dynamicAmount: Int = 20
) {
  companion object {
    val DEFAULT = BacklogSettings()
  }
}