package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable

class FormattedMessage(
  val id: Int,
  val time: CharSequence,
  val name: CharSequence? = null,
  val content: CharSequence? = null,
  val combined: CharSequence,
  val fallbackDrawable: Drawable? = null,
  val avatarUrl: String? = null,
  val isFollowUp: Boolean = false,
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean
)