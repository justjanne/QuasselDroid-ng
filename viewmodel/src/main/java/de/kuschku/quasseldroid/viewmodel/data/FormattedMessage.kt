package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable

class FormattedMessage(
  val id: Int,
  val time: CharSequence,
  val name: CharSequence? = null,
  val content: CharSequence? = null,
  val combined: CharSequence,
  val fallbackDrawable: Drawable? = null,
  val realName: CharSequence? = null,
  val avatarUrls: List<String> = emptyList(),
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean
)
