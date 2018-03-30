package de.kuschku.quasseldroid.ui.chat.messages

import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.quasseldroid.persistence.QuasselDatabase

data class DisplayMessage(
  val content: QuasselDatabase.DatabaseMessage,
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean
) {
  data class Tag(
    val id: MsgId,
    val isFollowUp: Boolean,
    val isSelected: Boolean,
    val isExpanded: Boolean,
    val isMarkerLine: Boolean
  )

  val tag = Tag(content.messageId, content.followUp, isSelected, isExpanded, isMarkerLine)
  val avatarUrl = content.sender.let {
    Regex("[us]id(\\d+)").matchEntire(HostmaskHelper.user(it))?.groupValues?.lastOrNull()?.let {
      "https://www.irccloud.com/avatar-redirect/$it"
    }
  }
}