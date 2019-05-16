/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.ui.chat.buffers

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Activity
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.lists.ListAdapter
import de.kuschku.quasseldroid.util.ui.fastscroll.views.FastScrollRecyclerView
import de.kuschku.quasseldroid.viewmodel.data.BufferListItem
import de.kuschku.quasseldroid.viewmodel.data.BufferProps
import de.kuschku.quasseldroid.viewmodel.data.BufferState
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import io.reactivex.subjects.BehaviorSubject

class BufferListAdapter(
  private val messageSettings: MessageSettings,
  private val selectedBuffer: BehaviorSubject<BufferId>,
  private val expandedNetworks: BehaviorSubject<Map<NetworkId, Boolean>>
) : ListAdapter<BufferListItem, BufferListAdapter.BufferViewHolder>(
  object : DiffUtil.ItemCallback<BufferListItem>() {
    override fun areItemsTheSame(oldItem: BufferListItem, newItem: BufferListItem) =
      oldItem.props.info.bufferId == newItem.props.info.bufferId

    override fun areContentsTheSame(oldItem: BufferListItem, newItem: BufferListItem) =
      oldItem == newItem
  }
), FastScrollRecyclerView.SectionedAdapter {
  override fun getSectionName(position: Int) = getItem(position).props.network.networkName

  private var clickListener: ((BufferId) -> Unit)? = null
  private var longClickListener: ((BufferId) -> Unit)? = null
  private var updateFinishedListener: ((List<BufferListItem>) -> Unit)? = null
  fun setOnClickListener(listener: ((BufferId) -> Unit)?) {
    this.clickListener = listener
  }

  fun setOnLongClickListener(listener: ((BufferId) -> Unit)?) {
    this.longClickListener = listener
  }

  fun setOnUpdateFinishedListener(listener: ((List<BufferListItem>) -> Unit)?) {
    this.updateFinishedListener = listener
  }

  override fun onUpdateFinished(list: List<BufferListItem>) {
    this.updateFinishedListener?.invoke(list)
  }

  fun expandListener(networkId: NetworkId, expand: Boolean) {
    expandedNetworks.onNext(expandedNetworks.value.orEmpty() + Pair(networkId, expand))
  }

  fun toggleSelection(buffer: BufferId): Boolean {
    val next = if (selectedBuffer.value == buffer) BufferId.MAX_VALUE else buffer
    selectedBuffer.onNext(next)
    return next != BufferId.MAX_VALUE
  }

  fun unselectAll() {
    selectedBuffer.onNext(BufferId.MAX_VALUE)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BufferViewHolder {
    val bufferType = viewType and 0xFFFF
    val bufferStatus = BufferStatus.of(((viewType ushr 16) and 0xFFFF).toShort())
    return when (bufferType) {
      BufferInfo.Type.ChannelBuffer.toInt() -> BufferViewHolder.ChannelBuffer(
        LayoutInflater.from(parent.context).inflate(
          R.layout.widget_buffer, parent, false
        ),
        clickListener = clickListener,
        longClickListener = longClickListener
      )
      BufferInfo.Type.QueryBuffer.toInt()   -> BufferViewHolder.QueryBuffer(
        LayoutInflater.from(parent.context).inflate(
          if (bufferStatus == BufferStatus.AWAY) R.layout.widget_buffer_away
          else R.layout.widget_buffer
          , parent, false
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
        "No such viewType: ${viewType.toString(16)}"
      )
    }
  }

  override fun onBindViewHolder(holder: BufferViewHolder, position: Int) =
    holder.bind(getItem(position).props, getItem(position).state, messageSettings)

  override fun getItemViewType(position: Int) = getItem(position).let {
    (it.props.bufferStatus.ordinal shl 16) + (it.props.info.type.toInt() and 0xFFFF)
  }

  abstract class BufferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(props: BufferProps, state: BufferState, messageSettings: MessageSettings)

    class StatusBuffer(
      itemView: View,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null,
      private val expansionListener: ((NetworkId, Boolean) -> Unit)? = null
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

      private var expanded: Boolean = false

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
            expansionListener?.invoke(network, !expanded)
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

      override fun bind(props: BufferProps, state: BufferState, messageSettings: MessageSettings) {
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

        this.expanded = state.networkExpanded

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

      override fun bind(props: BufferProps, state: BufferState, messageSettings: MessageSettings) {
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

        itemView.context.theme.styledAttributes(
          R.attr.colorTextPrimary, R.attr.colorTintActivity, R.attr.colorTintMessage,
          R.attr.colorTintHighlight
        ) {
          none = getColor(0, 0)
          activity = getColor(1, 0)
          message = getColor(2, 0)
          highlight = getColor(3, 0)
        }
      }

      override fun bind(props: BufferProps, state: BufferState, messageSettings: MessageSettings) {
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

        status.setImageDrawable(props.fallbackDrawable)
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

        itemView.context.theme.styledAttributes(
          R.attr.colorTextPrimary, R.attr.colorTintActivity, R.attr.colorTintMessage,
          R.attr.colorTintHighlight
        ) {
          none = getColor(0, 0)
          activity = getColor(1, 0)
          message = getColor(2, 0)
          highlight = getColor(3, 0)
        }
      }

      override fun bind(props: BufferProps, state: BufferState, messageSettings: MessageSettings) {
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

        status.loadAvatars(props.avatarUrls,
                           props.fallbackDrawable,
                           crop = !messageSettings.squareAvatars)
      }
    }
  }
}
