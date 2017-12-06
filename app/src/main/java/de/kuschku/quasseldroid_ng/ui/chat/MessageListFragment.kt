package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.arch.paging.PagedListAdapter
import android.os.Bundle
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.util.helper.switchMap
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment

class MessageListFragment : ServiceBoundFragment() {
  var currentBuffer: LiveData<BufferId?> = MutableLiveData()

  private lateinit var database: QuasselDatabase

  private val adapter = MessageAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    database = QuasselDatabase.Creator.init(context.applicationContext)
    val data = currentBuffer.switchMap {
      database.message().findByBufferIdPaged(it).create(Int.MAX_VALUE,
        PagedList.Config.Builder()
          .setPageSize(20)
          .setEnablePlaceholders(true)
          .setPrefetchDistance(20)
          .build()
      )
    }
    data.observe(this, Observer { list ->
      adapter.setList(list)
    })
  }

  class MessageAdapter : PagedListAdapter<QuasselDatabase.DatabaseMessage, MessageViewHolder>(MessageDiffCallback) {
    override fun onBindViewHolder(holder: MessageViewHolder?, position: Int) {
      holder?.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageViewHolder {
      return MessageViewHolder(LayoutInflater.from(parent?.context).inflate(android.R.layout.simple_list_item_1, parent, false))
    }
  }

  object MessageDiffCallback : DiffCallback<QuasselDatabase.DatabaseMessage>() {
    override fun areContentsTheSame(oldItem: QuasselDatabase.DatabaseMessage, newItem: QuasselDatabase.DatabaseMessage)
      = oldItem == newItem

    override fun areItemsTheSame(oldItem: QuasselDatabase.DatabaseMessage, newItem: QuasselDatabase.DatabaseMessage)
      = oldItem.messageId == newItem.messageId
  }

  class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(message: QuasselDatabase.DatabaseMessage?) {
      val text = (itemView as TextView)
      if (message == null) {
        text.text = "null"
      } else {
        text.text = "[${message.time}] <${message.senderPrefixes}${message.sender}> ${message.content}"
      }
    }
  }
}