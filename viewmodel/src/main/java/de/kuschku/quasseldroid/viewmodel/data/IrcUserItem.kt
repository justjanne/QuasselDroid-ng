package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable

data class IrcUserItem(
  val nick: String,
  val modes: String,
  val lowestMode: Int,
  val realname: CharSequence,
  val hostmask: String,
  val away: Boolean,
  val networkCasemapping: String?,
  val avatarUrls: List<String> = emptyList(),
  val fallbackDrawable: Drawable? = null,
  val displayNick: CharSequence? = null
)
