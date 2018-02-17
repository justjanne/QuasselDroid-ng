package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.util.LruCache
import android.view.LayoutInflater
import android.view.ViewGroup
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Flags
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.Message_Types
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.util.helper.getOrPut

class MessageAdapter(context: Context) :
  PagedListAdapter<QuasselDatabase.DatabaseMessage, QuasselMessageViewHolder>(
    QuasselDatabase.DatabaseMessage.MessageDiffCallback
  ) {
  private val messageRenderer: MessageRenderer = QuasselMessageRenderer(context)

  private val messageCache = LruCache<Int, FormattedMessage>(512)

  override fun onBindViewHolder(holder: QuasselMessageViewHolder, position: Int) {
    getItem(position)?.let {
      messageRenderer.bind(
        holder,
        messageCache.getOrPut(it.messageId) {
          messageRenderer.render(it)
        }
      )
    }
  }

  override fun getItemViewType(position: Int): Int {
    val item = getItem(position)
    if (item != null) {
      return viewType(Message_Flags.of(item.type), Message_Flags.of(item.flag))
    } else {
      return 0
    }
  }

  private fun viewType(type: Message_Types, flags: Message_Flags): Int {
    return (if (flags.hasFlag(Message_Flag.Highlight)) 0x8000 else 0x0000) or (type.value and 0x7FF)
  }

  override fun getItemId(position: Int): Long {
    return getItem(position)?.messageId?.toLong() ?: 0L
  }

  private fun messageType(viewType: Int): Message_Type?
    = Message_Type.of(viewType and 0x7FF).enabledValues().firstOrNull()

  private fun hasHiglight(viewType: Int)
    = viewType and 0x8000 != 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuasselMessageViewHolder {
    val messageType = messageType(viewType)
    val hasHighlight = hasHiglight(viewType)
    val viewHolder = QuasselMessageViewHolder(
      LayoutInflater.from(parent.context).inflate(
        messageRenderer.layout(messageType, hasHighlight),
        parent,
        false
      )
    )
    messageRenderer.init(viewHolder, messageType, hasHighlight)
    return viewHolder
  }

  operator fun get(position: Int): QuasselDatabase.DatabaseMessage? {
    return getItem(position)
  }
}

