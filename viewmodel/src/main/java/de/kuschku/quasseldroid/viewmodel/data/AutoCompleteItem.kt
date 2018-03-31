package de.kuschku.quasseldroid.viewmodel.data

import android.graphics.drawable.Drawable
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork

sealed class AutoCompleteItem(open val name: String) : Comparable<AutoCompleteItem> {
  override fun compareTo(other: AutoCompleteItem): Int {
    return when {
      this is UserItem &&
      other is ChannelItem -> -1
      this is ChannelItem &&
      other is UserItem    -> 1
      else                 -> this.name.compareTo(other.name)
    }
  }

  data class UserItem(
    val nick: String,
    val modes: String,
    val lowestMode: Int,
    val realname: CharSequence,
    val away: Boolean,
    val networkCasemapping: String?,
    val avatarUrl: String? = null,
    val fallbackDrawable: Drawable? = null,
    val displayNick: CharSequence? = null
  ) : AutoCompleteItem(nick)

  data class ChannelItem(
    val info: BufferInfo,
    val network: INetwork.NetworkInfo,
    val bufferStatus: BufferStatus,
    val description: CharSequence
  ) : AutoCompleteItem(info.bufferName ?: "")
}