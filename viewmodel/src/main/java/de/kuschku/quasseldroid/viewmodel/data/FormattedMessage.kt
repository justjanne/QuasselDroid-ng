package de.kuschku.quasseldroid.viewmodel.data

class FormattedMessage(
  val id: Int,
  val time: CharSequence,
  val content: CharSequence,
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean
)