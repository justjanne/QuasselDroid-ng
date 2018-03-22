package de.kuschku.quasseldroid.settings

data class BacklogSettings(
  val dynamicAmount: Int = 150
) {
  companion object {
    val DEFAULT = BacklogSettings()
  }
}