package de.kuschku.quasseldroid.settings

data class BacklogSettings(
  val pageSize: Int = 150,
  val initialAmount: Int = 20
) {
  companion object {
    val DEFAULT = BacklogSettings()
  }
}