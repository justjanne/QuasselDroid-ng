package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import de.kuschku.libquassel.protocol.Message_Flags
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase

class MessageAdapter(context: Context) : PagedListAdapter<QuasselDatabase.DatabaseMessage, QuasselMessageViewHolder>(QuasselDatabase.DatabaseMessage.MessageDiffCallback) {
  val messageRenderer: MessageRenderer = QuasselMessageRenderer(context)

  override fun onBindViewHolder(holder: QuasselMessageViewHolder, position: Int) {
    getItem(position)?.let { messageRenderer.bind(holder, it) }
  }

  override fun getItemViewType(position: Int): Int {
    return getItem(position)?.type ?: 0
  }

  private fun messageType(viewType: Int): Message_Type?
    = Message_Type.of(viewType).enabledValues().firstOrNull()

  private fun messageFlags(viewType: Int): Message_Flags
    = Message_Flags.of()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuasselMessageViewHolder {
    return QuasselMessageViewHolder(LayoutInflater.from(parent.context).inflate(
      messageRenderer.layout(messageType(viewType), messageFlags(viewType)),
      parent,
      false
    ))
  }
}

