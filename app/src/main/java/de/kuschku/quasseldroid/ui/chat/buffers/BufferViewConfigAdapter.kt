package de.kuschku.quasseldroid.ui.chat.buffers

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.ui.ContextThemeWrapper
import de.kuschku.quasseldroid.util.ui.RecyclerSpinnerAdapter

class BufferViewConfigAdapter :
  RecyclerSpinnerAdapter<BufferViewConfigAdapter.BufferViewConfigViewHolder>(),
  ThemedSpinnerAdapter {
  var data = emptyList<BufferViewConfig>()

  fun submitList(list: List<BufferViewConfig>) {
    data = list
    notifyDataSetChanged()
  }

  override fun isEmpty() = data.isEmpty()

  override fun onBindViewHolder(holder: BufferViewConfigViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean)
    : BufferViewConfigViewHolder {
    val inflater = LayoutInflater.from(
      if (dropDown)
        ContextThemeWrapper(parent.context, dropDownViewTheme)
      else
        parent.context
    )
    val view = inflater.inflate(R.layout.widget_spinner_item_toolbar, parent, false)
    return BufferViewConfigViewHolder(
      view
    )
  }

  override fun getItem(position: Int): BufferViewConfig? = when (position) {
    in (0 until data.size) -> data[position]
    else                   -> null
  }

  override fun getItemId(position: Int) = getItem(position)?.bufferViewId()?.toLong() ?: -1L

  override fun hasStableIds() = true

  override fun getCount() = data.size

  class BufferViewConfigViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(android.R.id.text1)
    lateinit var text: TextView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(bufferViewConfig: BufferViewConfig?) {
      text.text = bufferViewConfig?.bufferViewName() ?: ""
    }
  }
}
