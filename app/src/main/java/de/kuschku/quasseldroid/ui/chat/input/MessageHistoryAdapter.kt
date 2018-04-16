package de.kuschku.quasseldroid.ui.chat.input

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R

class MessageHistoryAdapter : ListAdapter<CharSequence, MessageHistoryAdapter.MessageViewHolder>(
  object : DiffUtil.ItemCallback<CharSequence>() {
    override fun areItemsTheSame(oldItem: CharSequence?, newItem: CharSequence?) =
      oldItem === newItem

    override fun areContentsTheSame(oldItem: CharSequence?, newItem: CharSequence?) =
      oldItem == newItem
  }) {
  private var clickListener: ((CharSequence) -> Unit)? = null

  fun setOnItemClickListener(listener: (CharSequence) -> Unit) {
    this.clickListener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    MessageViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.widget_history_message, parent, false),
      clickListener = clickListener
    )

  override fun onBindViewHolder(holder: MessageViewHolder, position: Int) =
    holder.bind(getItem(position))

  class MessageViewHolder(
    itemView: View,
    private val clickListener: ((CharSequence) -> Unit)? = null
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.content)
    lateinit var content: TextView

    var value: CharSequence? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        val value = value
        if (value != null)
          clickListener?.invoke(value)
      }
    }

    fun bind(data: CharSequence) {
      value = data
      content.text = data
    }
  }
}
