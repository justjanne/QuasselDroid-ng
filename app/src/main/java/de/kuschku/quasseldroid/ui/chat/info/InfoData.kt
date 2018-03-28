package de.kuschku.quasseldroid.ui.chat.info

import de.kuschku.libquassel.quassel.syncables.IrcChannel
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.quasseldroid.viewmodel.data.InfoGroup

data class InfoData(
  val type: InfoType,
  val user: IrcUser? = null,
  val channel: IrcChannel? = null,
  val network: Network,
  val properties: List<InfoGroup> = emptyList()
)