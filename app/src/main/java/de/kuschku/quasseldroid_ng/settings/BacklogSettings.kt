package de.kuschku.quasseldroid_ng.settings

data class BacklogSettings(
  val dynamicAmount: Int = 20
) {
  companion object {
    val DEFAULT = BacklogSettings()
  }
}