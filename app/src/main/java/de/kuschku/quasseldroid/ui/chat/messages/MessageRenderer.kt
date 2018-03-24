package de.kuschku.quasseldroid.ui.chat.messages

import android.content.Context
import android.support.annotation.LayoutRes
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.quasseldroid.persistence.QuasselDatabase

interface MessageRenderer {
  @LayoutRes
  fun layout(type: Message_Type?, hasHighlight: Boolean): Int

  fun bind(holder: QuasselMessageViewHolder, message: FormattedMessage)
  fun render(context: Context,
             message: QuasselDatabase.DatabaseMessage,
             markerLine: MsgId): FormattedMessage

  fun init(viewHolder: QuasselMessageViewHolder,
           messageType: Message_Type?,
           hasHighlight: Boolean) {
  }
}

