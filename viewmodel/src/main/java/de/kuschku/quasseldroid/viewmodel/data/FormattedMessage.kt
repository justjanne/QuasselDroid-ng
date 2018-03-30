package de.kuschku.quasseldroid.viewmodel.data

class FormattedMessage(
  val id: Int,
  val time: CharSequence,
  val name: CharSequence? = null,
  val content: CharSequence? = null,
  val combined: CharSequence,
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean
)