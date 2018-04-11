package de.kuschku.quasseldroid.ui.chat.messages

import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.quasseldroid.persistence.QuasselDatabase

data class DisplayMessage(
  val content: QuasselDatabase.DatabaseMessage,
  val hasDayChange: Boolean,
  val isFollowUp: Boolean,
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean,
  val isEmoji: Boolean
) {
  data class Tag(
    val id: MsgId,
    val hasDayChange: Boolean,
    val isFollowUp: Boolean,
    val isSelected: Boolean,
    val isExpanded: Boolean,
    val isMarkerLine: Boolean,
    val isEmoji: Boolean
  )

  val tag = Tag(
    content.messageId,
    hasDayChange,
    isFollowUp,
    isSelected,
    isExpanded,
    isMarkerLine,
    isEmoji
  )
}
