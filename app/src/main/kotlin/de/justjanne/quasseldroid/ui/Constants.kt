package de.justjanne.quasseldroid.ui

import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

object Constants {
  val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
}
