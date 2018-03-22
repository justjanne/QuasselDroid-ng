package de.kuschku.quasseldroid_ng.viewmodel.data

data class IrcUserItem(
  val nick: String,
  val modes: String,
  val lowestMode: Int,
  val realname: CharSequence,
  val away: Boolean,
  val networkCasemapping: String
)