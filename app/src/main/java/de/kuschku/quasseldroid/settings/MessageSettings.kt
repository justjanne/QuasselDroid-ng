package de.kuschku.quasseldroid.settings

data class MessageSettings(
  val showPrefix: ShowPrefixMode = ShowPrefixMode.HIGHEST,
  val colorizeNicknames: ColorizeNicknamesMode = ColorizeNicknamesMode.ALL_BUT_MINE,
  val colorizeMirc: Boolean = true,
  val useMonospace: Boolean = false,
  val textSize: Int = 14,
  val showSeconds: Boolean = false,
  val use24hClock: Boolean = true,
  val showHostmaskActions: Boolean = false,
  val showHostmaskPlain: Boolean = false,
  val nicksOnNewLine: Boolean = false,
  val timeAtEnd: Boolean = false,
  val showAvatars: Boolean = false,
  val largerEmoji: Boolean = false
) {

  enum class ColorizeNicknamesMode {
    ALL,
    ALL_BUT_MINE,
    NONE;

    companion object {
      private val map = values().associateBy { it.name }
      fun of(name: String) = map[name]
    }
  }

  enum class ShowPrefixMode {
    ALL,
    HIGHEST,
    NONE;

    companion object {
      private val map = values().associateBy { it.name }
      fun of(name: String) = map[name]
    }
  }

  companion object {
    val DEFAULT = MessageSettings()
  }
}
