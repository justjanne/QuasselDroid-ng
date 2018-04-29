/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat.buffers

import android.graphics.drawable.Drawable
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
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.getVectorDrawableCompat
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.tint
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.viewmodel.data.BufferListItem
import de.kuschku.quasseldroid.viewmodel.data.BufferProps
import de.kuschku.quasseldroid.viewmodel.data.BufferState
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import io.reactivex.subjects.BehaviorSubject

class BufferListAdapter(
  private val selectedBuffer: BehaviorSubject<BufferId>,
  private val collapsedNetworks: BehaviorSubject<Set<NetworkId>>
) : ListAdapter<BufferListItem, BufferListAdapter.BufferViewHolder>(
  object : DiffUtil.ItemCallback<BufferListItem>() {
    override fun areItemsTheSame(oldItem: BufferListItem, newItem: BufferListItem) =
      oldItem.props.info.bufferId == newItem.props.info.bufferId

    override fun areContentsTheSame(oldItem: BufferListItem, newItem: BufferListItem) =
      oldItem == newItem
  }
) {
  private var clickListener: ((BufferId) -> Unit)? = null
  private var longClickListener: ((BufferId) -> Unit)? = null
  fun setOnClickListener(listener: ((BufferId) -> Unit)?) {
    this.clickListener = listener
  }

  fun setOnLongClickListener(listener: ((BufferId) -> Unit)?) {
    this.longClickListener = listener
  }

  fun expandListener(networkId: NetworkId) {
    if (collapsedNetworks.value.orEmpty().contains(networkId))
      collapsedNetworks.onNext(collapsedNetworks.value.orEmpty() - networkId)
    else
      collapsedNetworks.onNext(collapsedNetworks.value.orEmpty() + networkId)
  }

  fun toggleSelection(buffer: BufferId): Boolean {
    val next = if (selectedBuffer.value == buffer) -1 else buffer
    selectedBuffer.onNext(next)
    return next != -1
  }

  fun unselectAll() {
    selectedBuffer.onNext(-1)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
    BufferInfo.Type.ChannelBuffer.toInt() -> BufferViewHolder.ChannelBuffer(
      LayoutInflater.from(parent.context).inflate(
        R.layout.widget_buffer, parent, false
      ),
      clickListener = clickListener,
      longClickListener = longClickListener
    )
    BufferInfo.Type.QueryBuffer.toInt()   -> BufferViewHolder.QueryBuffer(
      LayoutInflater.from(parent.context).inflate(
        R.layout.widget_buffer, parent, false
      ),
      clickListener = clickListener,
      longClickListener = longClickListener
    )
    BufferInfo.Type.GroupBuffer.toInt()   -> BufferViewHolder.GroupBuffer(
      LayoutInflater.from(parent.context).inflate(
        R.layout.widget_buffer, parent, false
      ),
      clickListener = clickListener,
      longClickListener = longClickListener
    )
    BufferInfo.Type.StatusBuffer.toInt()  -> BufferViewHolder.StatusBuffer(
      LayoutInflater.from(parent.context).inflate(
        R.layout.widget_network, parent, false
      ),
      clickListener = clickListener,
      longClickListener = longClickListener,
      expansionListener = ::expandListener
    )
    else                                  -> throw IllegalArgumentException(
      "No such viewType: $viewType"
    )
  }

  override fun onBindViewHolder(holder: BufferViewHolder, position: Int) =
    holder.bind(getItem(position).props, getItem(position).state)

  override fun getItemViewType(position: Int) = getItem(position).props.info.type.toInt()
  abstract class BufferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(props: BufferProps, state: BufferState)

    class StatusBuffer(
      itemView: View,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null,
      private val expansionListener: ((NetworkId) -> Unit)? = null
    ) : BufferViewHolder(itemView) {
      @BindView(R.id.status)
      lateinit var status: ImageView

      @BindView(R.id.name)
      lateinit var name: TextView

      var bufferId: BufferId? = null
      var networkId: NetworkId? = null

      private var none: Int = 0
      private var activity: Int = 0
      private var message: Int = 0
      private var highlight: Int = 0

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val buffer = bufferId
          if (buffer != null)
            clickListener?.invoke(buffer)
        }

        itemView.setOnLongClickListener {
          val buffer = bufferId
          if (buffer != null) {
            longClickListener?.invoke(buffer)
            true
          } else {
            false
          }
        }

        status.setOnClickListener {
          val network = networkId
          if (network != null)
            expansionListener?.invoke(network)
        }

        itemView.context.theme.styledAttributes(
          R.attr.colorTextSecondary, R.attr.colorTintActivity, R.attr.colorTintMessage,
          R.attr.colorTintHighlight
        ) {
          none = getColor(0, 0)
          activity = getColor(1, 0)
          message = getColor(2, 0)
          highlight = getColor(3, 0)
        }
      }

      override fun bind(props: BufferProps, state: BufferState) {
        name.text = props.network.networkName
        bufferId = props.info.bufferId
        networkId = props.info.networkId

        name.setTextColor(
          when {
            props.bufferActivity.hasFlag(Buffer_Activity.Highlight)     -> highlight
            props.bufferActivity.hasFlag(Buffer_Activity.NewMessage)    -> message
            props.bufferActivity.hasFlag(Buffer_Activity.OtherActivity) -> activity
            else                                                        -> none
          }
        )

        itemView.isSelected = state.selected

        if (state.networkExpanded) {
          status.setImageResource(R.drawable.ic_chevron_up)
        } else {
          status.setImageResource(R.drawable.ic_chevron_down)
        }
      }
    }

    class GroupBuffer(
      itemView: View,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null
    ) : BufferViewHolder(itemView) {
      @BindView(R.id.status)
      lateinit var status: ImageView

      @BindView(R.id.name)
      lateinit var name: TextView

      @BindView(R.id.description)
      lateinit var description: TextView

      var bufferId: BufferId? = null

      private val online: Drawable?
      private val offline: Drawable?

      private var none: Int = 0
      private var activity: Int = 0
      private var message: Int = 0
      private var highlight: Int = 0

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val buffer = bufferId
          if (buffer != null)
            clickListener?.invoke(buffer)
        }

        itemView.setOnLongClickListener {
          val buffer = bufferId
          if (buffer != null) {
            longClickListener?.invoke(buffer)
            true
          } else {
            false
          }
        }

        online = itemView.context.getVectorDrawableCompat(R.drawable.ic_status)?.mutate()
        offline = itemView.context.getVectorDrawableCompat(R.drawable.ic_status_offline)?.mutate()

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway,
          R.attr.colorTextPrimary, R.attr.colorTintActivity, R.attr.colorTintMessage,
          R.attr.colorTintHighlight
        ) {
          online?.tint(getColor(0, 0))
          offline?.tint(getColor(1, 0))

          none = getColor(2, 0)
          activity = getColor(3, 0)
          message = getColor(4, 0)
          highlight = getColor(5, 0)
        }
      }

      override fun bind(props: BufferProps, state: BufferState) {
        bufferId = props.info.bufferId

        name.text = props.info.bufferName
        description.text = props.description

        name.setTextColor(
          when {
            props.bufferActivity.hasFlag(Buffer_Activity.Highlight)     -> highlight
            props.bufferActivity.hasFlag(Buffer_Activity.NewMessage)    -> message
            props.bufferActivity.hasFlag(Buffer_Activity.OtherActivity) -> activity
            else                                                        -> none
          }
        )

        itemView.isSelected = state.selected

        description.visibleIf(props.description.isNotBlank())

        status.setImageDrawable(
          when (props.bufferStatus) {
            BufferStatus.ONLINE -> online
            else                -> offline
          }
        )
      }
    }

    class ChannelBuffer(
      itemView: View,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null
    ) : BufferViewHolder(itemView) {
      @BindView(R.id.status)
      lateinit var status: ImageView

      @BindView(R.id.name)
      lateinit var name: TextView

      @BindView(R.id.description)
      lateinit var description: TextView

      var bufferId: BufferId? = null

      private val online: Drawable?
      private val offline: Drawable?

      private var none: Int = 0
      private var activity: Int = 0
      private var message: Int = 0
      private var highlight: Int = 0

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val buffer = bufferId
          if (buffer != null)
            clickListener?.invoke(buffer)
        }

        itemView.setOnLongClickListener {
          val buffer = bufferId
          if (buffer != null) {
            longClickListener?.invoke(buffer)
            true
          } else {
            false
          }
        }

        online = itemView.context.getVectorDrawableCompat(R.drawable.ic_status_channel)?.mutate()
        offline = itemView.context.getVectorDrawableCompat(R.drawable.ic_status_channel_offline)?.mutate()

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway,
          R.attr.colorTextPrimary, R.attr.colorTintActivity, R.attr.colorTintMessage,
          R.attr.colorTintHighlight
        ) {
          online?.tint(getColor(0, 0))
          offline?.tint(getColor(1, 0))

          none = getColor(2, 0)
          activity = getColor(3, 0)
          message = getColor(4, 0)
          highlight = getColor(5, 0)
        }
      }

      override fun bind(props: BufferProps, state: BufferState) {
        bufferId = props.info.bufferId

        name.text = props.info.bufferName
        description.text = props.description

        name.setTextColor(
          when {
            props.bufferActivity.hasFlag(Buffer_Activity.Highlight)     -> highlight
            props.bufferActivity.hasFlag(Buffer_Activity.NewMessage)    -> message
            props.bufferActivity.hasFlag(Buffer_Activity.OtherActivity) -> activity
            else                                                        -> none
          }
        )

        itemView.isSelected = state.selected

        description.visibleIf(props.description.isNotBlank())

        status.setImageDrawable(
          when (props.bufferStatus) {
            BufferStatus.ONLINE -> online
            else                -> offline
          }
        )
      }
    }

    class QueryBuffer(
      itemView: View,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null
    ) : BufferViewHolder(itemView) {
      @BindView(R.id.status)
      lateinit var status: ImageView

      @BindView(R.id.name)
      lateinit var name: TextView

      @BindView(R.id.description)
      lateinit var description: TextView

      var bufferId: BufferId? = null

      private val online: Drawable?
      private val away: Drawable?
      private val offline: Drawable?

      private var none: Int = 0
      private var activity: Int = 0
      private var message: Int = 0
      private var highlight: Int = 0

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val buffer = bufferId
          if (buffer != null)
            clickListener?.invoke(buffer)
        }

        itemView.setOnLongClickListener {
          val buffer = bufferId
          if (buffer != null) {
            longClickListener?.invoke(buffer)
            true
          } else {
            false
          }
        }

        online = itemView.context.getVectorDrawableCompat(R.drawable.ic_status)?.mutate()
        away = itemView.context.getVectorDrawableCompat(R.drawable.ic_status)?.mutate()
        offline = itemView.context.getVectorDrawableCompat(R.drawable.ic_status_offline)?.mutate()

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway,
          R.attr.colorTextPrimary, R.attr.colorTintActivity, R.attr.colorTintMessage,
          R.attr.colorTintHighlight
        ) {
          online?.tint(getColor(0, 0))
          away?.tint(getColor(1, 0))
          offline?.tint(getColor(1, 0))

          none = getColor(2, 0)
          activity = getColor(3, 0)
          message = getColor(4, 0)
          highlight = getColor(5, 0)
        }
      }

      override fun bind(props: BufferProps, state: BufferState) {
        bufferId = props.info.bufferId

        name.text = props.info.bufferName
        description.text = props.description

        name.setTextColor(
          when {
            props.bufferActivity.hasFlag(Buffer_Activity.Highlight)     -> highlight
            props.bufferActivity.hasFlag(Buffer_Activity.NewMessage)    -> message
            props.bufferActivity.hasFlag(Buffer_Activity.OtherActivity) -> activity
            else                                                        -> none
          }
        )

        itemView.isSelected = state.selected

        description.visibleIf(props.description.isNotBlank())

        status.setImageDrawable(
          when (props.bufferStatus) {
            BufferStatus.ONLINE -> online
            BufferStatus.AWAY   -> away
            else                -> offline
          }
        )
      }
    }
  }
}
