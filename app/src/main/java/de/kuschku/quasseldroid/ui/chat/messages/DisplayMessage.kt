package de.kuschku.quasseldroid.ui.chat.messages

import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.quasseldroid.persistence.QuasselDatabase

data class DisplayMessage(
  val content: QuasselDatabase.DatabaseMessage,
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean
) {
  data class Tag(
    val id: MsgId,
    val isSelected: Boolean,
    val isExpanded: Boolean,
    val isMarkerLine: Boolean
  )

  val tag = Tag(content.messageId, isSelected, isExpanded, isMarkerLine)
}