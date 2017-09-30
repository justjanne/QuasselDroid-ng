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
import de.kuschku.libquassel.quassel.BufferInfo

class BufferListAdapter(
  lifecycleOwner: LifecycleOwner,
  liveData: LiveData<List<BufferInfo>?>,
  runInBackground: (() -> Unit) -> Any,
  runOnUiThread: (Runnable) -> Any
) : RecyclerView.Adapter<BufferListAdapter.BufferViewHolder>() {
  var data = mutableListOf<BufferInfo>()

  init {
    liveData.observe(lifecycleOwner, Observer { list: List<BufferInfo>? ->
      runInBackground {
        val old = data
        val new = list ?: emptyList()
        val result = DiffUtil.calculateDiff(
          object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
              = old[oldItemPosition].bufferId == new[newItemPosition].bufferId

            override fun getOldListSize() = old.size
            override fun getNewListSize() = new.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
              = old[oldItemPosition] == new[newItemPosition]
          }, true)
        runOnUiThread(Runnable {
          data.clear()
          data.addAll(new)
          result.dispatchUpdatesTo(this@BufferListAdapter)
        })
      }
    })
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BufferViewHolder(
    LayoutInflater.from(parent.context)
      .inflate(android.R.layout.simple_list_item_1, parent, false)
  )

  override fun onBindViewHolder(holder: BufferViewHolder, position: Int)
    = holder.bind(data[position])

  override fun getItemCount() = data.size

  class BufferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(android.R.id.text1)
    lateinit var text: TextView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(info: BufferInfo) {
      text.text = "${info.networkId}/${info.bufferName}"
    }
  }
}
