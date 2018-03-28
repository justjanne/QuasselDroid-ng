package de.kuschku.quasseldroid.ui.chat.info

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R

class InfoPropertyActionAdapter :
  ListAdapter<InfoPropertyAction, InfoPropertyActionAdapter.InfoPropertyActionViewHolder>(
    object : DiffUtil.ItemCallback<InfoPropertyAction>() {
      override fun areItemsTheSame(oldItem: InfoPropertyAction, newItem: InfoPropertyAction) =
        oldItem.name == newItem.name

      override fun areContentsTheSame(oldItem: InfoPropertyAction, newItem: InfoPropertyAction) =
        oldItem == newItem
    }
  ) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InfoPropertyActionViewHolder(
    LayoutInflater.from(parent.context).inflate(
      if (viewType == VIEWTYPE_FEATURED)
        R.layout.widget_info_action_main
      else
        R.layout.widget_info_action,
      parent,
      false
    )
  )

  override fun onBindViewHolder(holder: InfoPropertyActionViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun getItemViewType(position: Int) =
    if (getItem(position).featured) VIEWTYPE_FEATURED else VIEWTYPE_NORMAL

  class InfoPropertyActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.button)
    lateinit var button: Button

    private var onClick: (() -> Unit)? = null

    init {
      ButterKnife.bind(this, itemView)
      button.setOnClickListener {
        onClick?.invoke()
      }
    }

    fun bind(item: InfoPropertyAction) {
      this.onClick = item.onClick

      button.text = item.name
    }
  }

  companion object {
    const val VIEWTYPE_NORMAL = 0
    const val VIEWTYPE_FEATURED = 1
  }
}