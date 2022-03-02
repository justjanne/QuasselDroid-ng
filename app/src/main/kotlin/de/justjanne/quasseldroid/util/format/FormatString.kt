package de.justjanne.quasseldroid.util.format

sealed class FormatString {
  data class FixedValue(
    val content: CharSequence
  ) : FormatString() {
    override fun toString(): String {
      return "FixedValue($content)"
    }
  }

  data class FormatSpecifier(
    val index: Int?,
    val flags: String?,
    val width: Int?,
    val precision: Int?,
    val time: Boolean,
    val conversion: Char
  ) : FormatString() {
    override fun toString(): String = listOfNotNull(
      index?.let { "index=$index" },
      flags?.let { "flags='$flags'" },
      width?.let { "width=$width" },
      precision?.let { "precision=$precision" },
      "time=$time",
      "conversion='$conversion'"
    ).joinToString(", ", prefix = "FormatSpecifier(", postfix = ")")

    fun toFormatSpecifier(ignoreFlags: Set<Char> = emptySet()) = buildString {
      append("%")
      if (index != null) {
        append(index)
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
