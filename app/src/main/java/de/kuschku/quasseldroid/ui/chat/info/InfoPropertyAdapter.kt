package de.kuschku.quasseldroid.ui.chat.info

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.viewmodel.data.InfoProperty
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class InfoPropertyAdapter :
  ListAdapter<InfoProperty, InfoPropertyAdapter.InfoPropertyViewHolder>(
    object : DiffUtil.ItemCallback<InfoProperty>() {
      override fun areItemsTheSame(oldItem: InfoProperty, newItem: InfoProperty) =
        oldItem.name == newItem.name

      override fun areContentsTheSame(oldItem: InfoProperty, newItem: InfoProperty) =
        oldItem == newItem
    }
  ) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InfoPropertyViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.widget_userinfo, parent, false)
  )

  override fun onBindViewHolder(holder: InfoPropertyViewHolder, position: Int) =
    holder.bind(getItem(position))

  class InfoPropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.icon_frame)
    lateinit var iconFrame: View

    @BindView(R.id.icon)
    lateinit var icon: ImageView

    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.value)
    lateinit var value: TextView

    init {
      ButterKnife.bind(this, itemView)
      value.movementMethod = BetterLinkMovementMethod.getInstance()
    }

    fun bind(item: InfoProperty) {
      item.icon?.let(icon::setImageResource)
      name.text = item.name
      value.text = item.value

      iconFrame.visibleIf(item.icon != null)
    }
  }
}