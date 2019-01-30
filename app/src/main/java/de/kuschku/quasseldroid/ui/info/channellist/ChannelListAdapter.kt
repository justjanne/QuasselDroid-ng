/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.info.channellist

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.IrcListHelper
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.ColorContext
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import javax.inject.Inject

class ChannelListAdapter @Inject constructor(
  private val contentFormatter: ContentFormatter,
  context: Context,
  colorContext: ColorContext
) :
  ListAdapter<IrcListHelper.ChannelDescription, ChannelListAdapter.ChannelViewHolder>(
    object : DiffUtil.ItemCallback<IrcListHelper.ChannelDescription>() {
      override fun areItemsTheSame(oldItem: IrcListHelper.ChannelDescription,
                                   newItem: IrcListHelper.ChannelDescription) =
        oldItem.channelName == newItem.channelName

      override fun areContentsTheSame(oldItem: IrcListHelper.ChannelDescription,
                                      newItem: IrcListHelper.ChannelDescription) =
        oldItem == newItem
    }
  ) {
  val colorAccent = context.theme.styledAttributes(R.attr.colorAccent) {
    getColor(0, 0)
  }

  private val fallbackDrawable = colorContext.buildTextDrawable("#", colorAccent)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
    return ChannelViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.widget_channel_search, parent, false),
      contentFormatter,
      fallbackDrawable
    )
  }

  override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  class ChannelViewHolder(
    itemView: View,
    private val contentFormatter: ContentFormatter,
    fallbackDrawable: Drawable
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.status)
    lateinit var status: ImageView

    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.users)
    lateinit var users: TextView

    @BindView(R.id.topic)
    lateinit var topic: TextView

    private var data: IrcListHelper.ChannelDescription? = null

    init {
      ButterKnife.bind(this, itemView)
      status.setImageDrawable(fallbackDrawable)
      itemView.setOnClickListener {
        data?.let {
          ChatActivity.launch(
            itemView.context,
            networkId = it.netId,
            channel = it.channelName
          )
        }
      }
    }

    fun bind(data: IrcListHelper.ChannelDescription) {
      name.text = data.channelName
      topic.text = contentFormatter.formatContent(data.topic, networkId = data.netId)
      users.text = itemView.context.resources.getQuantityString(R.plurals.label_user_count,
                                                                data.userCount.toInt(),
                                                                data.userCount.toInt())

      this.data = data
    }
  }
}
