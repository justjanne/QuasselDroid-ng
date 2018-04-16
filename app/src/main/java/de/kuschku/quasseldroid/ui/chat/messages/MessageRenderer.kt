package de.kuschku.quasseldroid.ui.chat.messages

import android.content.Context
import android.support.annotation.LayoutRes
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage

interface MessageRenderer {
  @LayoutRes
  fun layout(type: Message_Type?, hasHighlight: Boolean, isFollowUp: Boolean, isEmoji: Boolean): Int

  fun bind(holder: MessageAdapter.QuasselMessageViewHolder, message: FormattedMessage,
           original: QuasselDatabase.DatabaseMessage)

  fun render(context: Context, message: DisplayMessage): FormattedMessage

  fun init(viewHolder: MessageAdapter.QuasselMessageViewHolder,
           messageType: Message_Type?,
           hasHighlight: Boolean,
           isFollowUp: Boolean,
           isEmoji: Boolean) = Unit
}
