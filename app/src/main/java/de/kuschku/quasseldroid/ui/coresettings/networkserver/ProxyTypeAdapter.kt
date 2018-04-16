package de.kuschku.quasseldroid.ui.coresettings.networkserver

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.ui.ContextThemeWrapper
import de.kuschku.quasseldroid.util.ui.RecyclerSpinnerAdapter

class ProxyTypeAdapter(val data: List<ProxyTypeItem>) :
  RecyclerSpinnerAdapter<ProxyTypeAdapter.ProxyTypeViewHolder>(),
  ThemedSpinnerAdapter {

  override fun isEmpty() = data.isEmpty()

  override fun onBindViewHolder(holder: ProxyTypeViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean)
    : ProxyTypeViewHolder {
    val inflater = LayoutInflater.from(
      if (dropDown) ContextThemeWrapper(parent.context, dropDownViewTheme)
      else parent.context
    )
    return ProxyTypeViewHolder(
      inflater.inflate(R.layout.widget_spinner_item_toolbar, parent, false)
    )
  }

  override fun getItem(position: Int) = data[position]

  override fun getItemId(position: Int) = getItem(position).value.value.toLong()

  override fun hasStableIds() = true

  override fun getCount() = data.size

  fun indexOf(value: Int): Int? {
    for ((key, item) in data.withIndex()) {
      if (item.value.value == value) {
        return key
      }
    }
    return null
  }

  class ProxyTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(android.R.id.text1)
    lateinit var text: TextView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(activity: ProxyTypeItem?) {
      activity?.let {
        text.setText(it.name)
      }
    }
  }
}

