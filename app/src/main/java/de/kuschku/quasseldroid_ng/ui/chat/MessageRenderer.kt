package de.kuschku.quasseldroid_ng.ui.chat

import android.support.annotation.LayoutRes
import de.kuschku.libquassel.protocol.Message_Flags
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase

interface MessageRenderer {
  @LayoutRes
  fun layout(type: Message_Type?, flags: Message_Flags): Int

  fun bind(holder: QuasselMessageViewHolder, message: QuasselDatabase.DatabaseMessage)
}