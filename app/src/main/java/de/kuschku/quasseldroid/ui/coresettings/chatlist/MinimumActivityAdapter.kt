package de.kuschku.quasseldroid.ui.coresettings.chatlist

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Activities
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.ui.ContextThemeWrapper
import de.kuschku.quasseldroid.util.ui.RecyclerSpinnerAdapter

class MinimumActivityAdapter(val data: List<MinimumActivityItem>) :
  RecyclerSpinnerAdapter<MinimumActivityAdapter.MinimumActivityViewHolder>(),
  ThemedSpinnerAdapter {

  override fun isEmpty() = data.isEmpty()

  override fun onBindViewHolder(holder: MinimumActivityViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean)
    : MinimumActivityViewHolder {
    val inflater = LayoutInflater.from(
      if (dropDown)
        ContextThemeWrapper(parent.context, dropDownViewTheme)
      else
        parent.context
    )
    val view = inflater.inflate(R.layout.widget_spinner_item_toolbar, parent, false)
    return MinimumActivityViewHolder(
      view
    )
  }

  override fun getItem(position: Int) = data[position]

  override fun getItemId(position: Int) = getItem(position).activity.toInt().toLong()

  override fun hasStableIds() = true

  override fun getCount() = data.size

  fun indexOf(activity: Buffer_Activities): Int? {
    for ((key, item) in data.withIndex()) {
      if (item.activity.toInt() == activity.toInt()) {
        return key
      }
    }
    return null
  }

  class MinimumActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(android.R.id.text1)
    lateinit var text: TextView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(activity: MinimumActivityItem?) {
      activity?.let {
        text.setText(it.name)
      }
    }
  }
}
