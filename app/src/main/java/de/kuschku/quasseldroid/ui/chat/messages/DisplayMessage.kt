/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat.messages

import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.quasseldroid.persistence.models.MessageData

data class DisplayMessage(
  val content: MessageData,
  val hasDayChange: Boolean,
  val isFollowUp: Boolean,
  val isSelected: Boolean,
  val isExpanded: Boolean,
  val isMarkerLine: Boolean,
  val isEmoji: Boolean
) {
  data class Tag(
    val id: MsgId,
    val isSelf: Boolean,
    val hasDayChange: Boolean,
    val isFollowUp: Boolean,
    val isSelected: Boolean,
    val isExpanded: Boolean,
    val isMarkerLine: Boolean,
    val isEmoji: Boolean
  )

  val tag = Tag(
    content.messageId,
    content.flag.hasFlag(Message_Flag.Self),
    hasDayChange,
    isFollowUp,
    isSelected,
    isExpanded,
    isMarkerLine,
    isEmoji
  )
}
