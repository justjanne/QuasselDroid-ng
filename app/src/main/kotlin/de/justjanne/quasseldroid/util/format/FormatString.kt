package de.justjanne.quasseldroid.util.format

import de.justjanne.quasseldroid.util.extensions.joinString

sealed class FormatString {
  data class FixedValue(
    val content: CharSequence
  ) : FormatString() {
    override fun toString(): String {
      return "FixedValue($content)"
    }
  }

  data class FormatSpecifier(
    val argumentIndex: Int? = null,
    val flags: String? = null,
    val width: Int? = null,
    val precision: Int? = null,
    val time: Boolean = false,
    val conversion: Char
  ) : FormatString() {
    override fun toString(): String = joinString(", ", "FormatSpecifier(", ")") {
      if (argumentIndex != null) {
        append("argumentIndex=$argumentIndex")
      }
      if (flags != null) {
        append("flags=$flags")
      }
      if (width != null) {
        append("width=$width")
      }
      if (precision != null) {
        append("precision=$precision")
      }
      append("time=$time")
      append("conversion=$conversion")
    }

    fun toFormatSpecifier(ignoreFlags: Set<Char> = emptySet()) = buildString {
      append("%")
      if (argumentIndex != null) {
        append(argumentIndex)
        append("$")
      }
      if (flags != null) {
        append(flags.filterNot(ignoreFlags::contains))
      }
      if (width != null) {
        append(width)
      }
      if (precision != null) {
        append('.')
        append(precision)
      }
      if (time) {
        append("t")
      }
      append(conversion)
    }
  }
}
