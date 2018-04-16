package de.kuschku.quasseldroid.ui.coresettings.ignoreitem

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.ui.ContextThemeWrapper
import de.kuschku.quasseldroid.util.ui.RecyclerSpinnerAdapter

class ScopeTypeAdapter(val data: List<ScopeTypeItem>) :
  RecyclerSpinnerAdapter<ScopeTypeAdapter.ScopeTypeViewHolder>(),
  ThemedSpinnerAdapter {

  override fun isEmpty() = data.isEmpty()

  override fun onBindViewHolder(holder: ScopeTypeViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean)
    : ScopeTypeViewHolder {
    val inflater = LayoutInflater.from(
      if (dropDown)
        ContextThemeWrapper(parent.context, dropDownViewTheme)
      else
        parent.context
    )
    val view = inflater.inflate(R.layout.widget_spinner_item_toolbar, parent, false)
    return ScopeTypeViewHolder(
      view
    )
  }

  override fun getItem(position: Int) = data[position]

  override fun getItemId(position: Int) = getItem(position).value.value.toLong()

  override fun hasStableIds() = true

  override fun getCount() = data.size

  fun indexOf(value: IgnoreListManager.ScopeType): Int? {
    for ((key, item) in data.withIndex()) {
      if (item.value.value == value.value) {
        return key
      }
    }
    return null
  }

  class ScopeTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(android.R.id.text1)
    lateinit var text: TextView

    init {
      ButterKnife.bind(this, itemView)
    }

    fun bind(activity: ScopeTypeItem?) {
      activity?.let {
        text.setText(it.name)
      }
    }
  }
}
