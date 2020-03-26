/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.quasseldroid.ui.info.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.quasseldroid.databinding.WidgetBufferBinding
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.lists.ListAdapter
import de.kuschku.quasseldroid.viewmodel.data.BufferProps

class ChannelAdapter : ListAdapter<BufferProps, ChannelAdapter.ChannelViewHolder>(
  object : DiffUtil.ItemCallback<BufferProps>() {
    override fun areItemsTheSame(oldItem: BufferProps, newItem: BufferProps) =
      oldItem.info.bufferId == newItem.info.bufferId

    override fun areContentsTheSame(oldItem: BufferProps, newItem: BufferProps) =
      oldItem == newItem
  }
) {
  private var clickListener: ((NetworkId, String) -> Unit)? = null
  fun setOnClickListener(listener: ((NetworkId, String) -> Unit)?) {
    this.clickListener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChannelViewHolder(
    WidgetBufferBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    clickListener = clickListener
  )

  override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) =
    holder.bind(getItem(position))

  class ChannelViewHolder(
    private val binding: WidgetBufferBinding,
    private val clickListener: ((NetworkId, String) -> Unit)? = null
  ) : RecyclerView.ViewHolder(binding.root) {
    var info: BufferInfo? = null

    init {
      itemView.setOnClickListener {
        info?.let {
          ChatActivity.launch(
            itemView.context,
            networkId = it.networkId,
            channel = it.bufferName
          )
        }
      }
    }

    fun bind(props: BufferProps) {
      info = props.info

      binding.name.text = props.info.bufferName
      binding.description.text = props.description

      binding.description.visibleIf(props.description.isNotBlank())

      binding.status.setImageDrawable(props.fallbackDrawable)
    }
  }
}

