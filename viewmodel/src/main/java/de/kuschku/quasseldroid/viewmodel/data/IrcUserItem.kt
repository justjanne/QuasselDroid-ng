package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable

data class IrcUserItem(
  val nick: String,
  val modes: String,
  val lowestMode: Int,
  val realname: CharSequence,
  val away: Boolean,
  val networkCasemapping: String?,
  val avatarUrl: String? = null,
  val fallbackDrawable: Drawable? = null,
  val displayNick: CharSequence? = null
)