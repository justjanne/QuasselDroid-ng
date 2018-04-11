package de.kuschku.quasseldroid.settings

data class AutoCompleteSettings(
  val senderDoubleClick: Boolean = true,
  val button: Boolean = false,
  val doubleTap: Boolean = true,
  val auto: Boolean = false,
  val prefix: Boolean = true
) {
  companion object {
    val DEFAULT = AutoCompleteSettings()
  }
}
