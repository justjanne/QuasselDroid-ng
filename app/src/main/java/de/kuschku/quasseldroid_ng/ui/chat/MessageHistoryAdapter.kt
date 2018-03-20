package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R

class MessageHistoryAdapter(
  lifecycleOwner: LifecycleOwner,
  liveData: LiveData<List<CharSequence>?>,
  runInBackground: (() -> Unit) -> Any,
  runOnUiThread: (Runnable) -> Any,
  private val clickListener: ((CharSequence) -> Unit)? = null
) : RecyclerView.Adapter<MessageHistoryAdapter.MessageViewHolder>() {
  var data = mutableListOf<CharSequence>()

  init {
    liveData.observe(lifecycleOwner, Observer { it: List<CharSequence>? ->
      runInBackground {
        val list = it ?: emptyList()
        val old: List<CharSequence> = data
        val new: List<CharSequence> = list
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
          override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition] == new[newItemPosition]

          override fun getOldListSize() = old.size
          override fun getNewListSize() = new.size
          override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            old[oldItemPosition] == new[newItemPosition]
        }, true)
        runOnUiThread(Runnable {
          data.clear()
          data.addAll(new)
          result.dispatchUpdatesTo(this@MessageHistoryAdapter)
        })
      }
    })
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MessageViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.widget_history_message, parent, false),
    clickListener = clickListener
  )

  override fun onBindViewHolder(holder: MessageViewHolder, position: Int) =
    holder.bind(data[position])

  override fun getItemCount() = data.size

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