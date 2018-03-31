package de.kuschku.quasseldroid.viewmodel.data

data class IrcUserItem(
  val nick: String,
  val modes: String,
  val lowestMode: Int,
  val realname: CharSequence,
  val away: Boolean,
  val networkCasemapping: String?
)