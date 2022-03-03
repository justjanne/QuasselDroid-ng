package de.justjanne.quasseldroid.util.backport

import java.io.Serializable

class StringJoiner(
  private val delimiter: String,
  private val prefix: String = "",
  private val suffix: String = ""
) : Serializable, Appendable {
  private val builder = StringBuilder()

  override fun append(data: CharSequence?, start: Int, end: Int): StringJoiner =
    this.apply { prepareBuilder().append(data, start, end) }

  override fun append(data: CharSequence?): StringJoiner =
    this.apply { prepareBuilder().append(data) }

  override fun append(data: Char): StringJoiner =
    this.apply { prepareBuilder().append(data) }

  private fun prepareBuilder(): StringBuilder = builder.apply {
    append(if (isEmpty()) prefix else delimiter)
  }

  override fun toString(): String =
    if (builder.isEmpty()) {
      prefix + suffix
    } else {
      val length = builder.length
      builder.append(suffix)
      val result = builder.toString()
      builder.setLength(length)
      result
    }

  fun length(): Int =
    if (builder.isEmpty()) prefix.length + suffix.length
    else builder.length + suffix.length
}
