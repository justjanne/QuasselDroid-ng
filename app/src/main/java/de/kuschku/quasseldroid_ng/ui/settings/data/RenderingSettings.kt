package de.kuschku.quasseldroid_ng.ui.settings.data

data class RenderingSettings(
  val showPrefix: ShowPrefixMode = ShowPrefixMode.FIRST,
  val colorizeNicknames: ColorizeNicknamesMode = ColorizeNicknamesMode.ALL_BUT_MINE,
  val timeFormat: String = ""
) {
  enum class ColorizeNicknamesMode(val value: Int) {
    ALL(0),
    ALL_BUT_MINE(1),
    NONE(2);

    companion object {
      fun of(value: Int) = when (value) {
        0    -> ALL
        1    -> ALL_BUT_MINE
        2    -> NONE
        else -> ALL_BUT_MINE
      }
    }
  }

  enum class ShowPrefixMode(val value: Int) {
    ALL(0),
    FIRST(1),
    NONE(2);

    companion object {
      fun of(value: Int) = when (value) {
        0    -> ALL
        1    -> FIRST
        2    -> NONE
        else -> FIRST
      }
    }
  }
}