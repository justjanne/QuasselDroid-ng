package de.kuschku.quasseldroid.settings

data class AutoCompleteSettings(
  val button: Boolean = false,
  val doubleTap: Boolean = true,
  val auto: Boolean = true,
  val prefix: Boolean = true
) {
  companion object {
    val DEFAULT = AutoCompleteSettings()
  }
}