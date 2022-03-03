package de.justjanne.quasseldroid.util.format

import de.justjanne.quasseldroid.util.extensions.joinString
import androidx.compose.ui.graphics.Color as AndroidColor

object IrcFormat {
  data class Span(
    val content: String,
    val style: Style = Style()
  ) {
    override fun toString(): String = joinString(", ", "Info(", ")") {
      append(content)
      if (style != Style()) {
        append("style=$style")
      }
    }
  }

  data class Style(
    val flags: Set<Flag> = emptySet(),
    val foreground: Color? = null,
    val background: Color? = null,
  ) {
    fun flipFlag(flag: Flag) = copy(
      flags = if (flags.contains(flag)) flags - flag else flags + flag
    )

    override fun toString(): String = joinString(", ", "Info(", ")") {
      if (flags.isNotEmpty()) {
        append("flags=$flags")
      }
      if (foreground != null) {
        append("foreground=$foreground")
      }
      if (background != null) {
        append("background=$background")
      }
    }
  }

  sealed class Color {
    data class Mirc(val index: Int) : Color() {
      override fun toString(): String = "Mirc($index)"
    }

    data class Hex(val color: AndroidColor) : Color() {
      override fun toString(): String = "Hex(#${color.value.toString(16)})"
    }
  }

  enum class Flag {
    BOLD,
    ITALIC,
    UNDERLINE,
    STRIKETHROUGH,
    MONOSPACE,
    INVERSE
  }
}
