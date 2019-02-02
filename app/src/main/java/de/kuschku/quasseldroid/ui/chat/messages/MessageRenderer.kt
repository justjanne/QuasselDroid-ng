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

import android.content.Context
import androidx.annotation.LayoutRes
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.quasseldroid.persistence.models.MessageData
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage

interface MessageRenderer {
  @LayoutRes
  fun layout(type: Message_Type?,
             hasHighlight: Boolean,
             isFollowUp: Boolean,
             isEmoji: Boolean,
             isSelf: Boolean): Int

  fun bind(holder: MessageAdapter.QuasselMessageViewHolder, message: FormattedMessage,
           original: MessageData)

  fun render(context: Context, message: DisplayMessage): FormattedMessage

  fun init(viewHolder: MessageAdapter.QuasselMessageViewHolder,
           messageType: Message_Type?,
           hasHighlight: Boolean,
           isFollowUp: Boolean,
           isEmoji: Boolean,
           isSelf: Boolean) = Unit
}
