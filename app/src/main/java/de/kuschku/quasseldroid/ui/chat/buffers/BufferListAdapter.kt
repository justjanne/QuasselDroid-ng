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

package de.kuschku.quasseldroid.ui.chat.buffers

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.safeValue
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.databinding.WidgetBufferAwayBinding
import de.kuschku.quasseldroid.databinding.WidgetBufferBinding
import de.kuschku.quasseldroid.databinding.WidgetNetworkBinding
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.lists.ListAdapter
import de.kuschku.quasseldroid.util.ui.fastscroll.views.FastScrollRecyclerView
import de.kuschku.quasseldroid.viewmodel.data.BufferListItem
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import io.reactivex.subjects.BehaviorSubject

@SuppressLint("ClickableViewAccessibility", "ResourceType")
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
  fun setOnClickListener(listener: ((BufferId) -> Unit)?) {
    this.clickListener = listener
  }

  private var longClickListener: ((BufferId) -> Unit)? = null
  fun setOnLongClickListener(listener: ((BufferId) -> Unit)?) {
    this.longClickListener = listener
  }

  private var dragListener: ((BufferViewHolder) -> Unit)? = null
  fun setOnDragListener(listener: ((BufferViewHolder) -> Unit)?) {
    dragListener = listener
  }

  private var updateFinishedListener: ((List<BufferListItem>) -> Unit)? = null
  fun setOnUpdateFinishedListener(listener: ((List<BufferListItem>) -> Unit)?) {
    this.updateFinishedListener = listener
  }

  override fun onUpdateFinished(list: List<BufferListItem>) {
    this.updateFinishedListener?.invoke(list)
  }

  fun expandListener(networkId: NetworkId, expand: Boolean) {
    expandedNetworks.onNext(expandedNetworks.safeValue + Pair(networkId, expand))
  }

  fun unselectAll() {
    selectedBuffer.onNext(BufferId.MAX_VALUE)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BufferViewHolder {
    val viewType = ViewType(viewType.toUInt())
    return when (viewType.bufferType?.enabledValues()?.firstOrNull()) {
      BufferInfo.Type.ChannelBuffer -> BufferViewHolder.ChannelBuffer(
        WidgetBufferBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        clickListener = clickListener,
        longClickListener = longClickListener,
        dragListener = dragListener
      )
      BufferInfo.Type.QueryBuffer   -> if (viewType.bufferStatus == BufferStatus.AWAY) {
        BufferViewHolder.QueryAwayBuffer(
          WidgetBufferAwayBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
          ),
          clickListener = clickListener,
          longClickListener = longClickListener,
          dragListener = dragListener
        )
      } else {
        BufferViewHolder.QueryBuffer(
          WidgetBufferBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
          ),
          clickListener = clickListener,
          longClickListener = longClickListener,
          dragListener = dragListener
        )
      }
      BufferInfo.Type.GroupBuffer   -> BufferViewHolder.GroupBuffer(
        WidgetBufferBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        clickListener = clickListener,
        longClickListener = longClickListener,
        dragListener = dragListener
      )
      BufferInfo.Type.StatusBuffer  -> BufferViewHolder.StatusBuffer(
        WidgetNetworkBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        clickListener = clickListener,
        longClickListener = longClickListener,
        expansionListener = ::expandListener
      )
      else                          ->
        throw IllegalArgumentException("No such viewType: $viewType")
    }
  }

  override fun onBindViewHolder(holder: BufferViewHolder, position: Int) =
    holder.bind(getItem(position), messageSettings)

  data class ViewType(
    val bufferStatus: BufferStatus?,
    val bufferType: Buffer_Types?
  ) {
    constructor(item: BufferListItem) : this(
      bufferStatus = item.props.bufferStatus,
      bufferType = item.props.info.type
    )

    constructor(viewType: UInt) : this(
      bufferStatus = BufferStatus.of(viewType.shr(16).and(0xFFu).toUByte()),
      bufferType = Buffer_Type.of(viewType.and(0xFFFFu).toUShort())
    )

    fun compute(): UInt {
      val bufferStatusValue = bufferStatus?.value ?: 0xFFu
      val bufferTypeValue = bufferType?.value ?: 0xFFu
      return bufferStatusValue.toUInt().shl(16) +
             bufferTypeValue
    }
  }

  override fun getItemViewType(position: Int) = ViewType(getItem(position)).compute().toInt()

  abstract class BufferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: BufferListItem, messageSettings: MessageSettings)

    class StatusBuffer(
      private val binding: WidgetNetworkBinding,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null,
      private val expansionListener: ((NetworkId, Boolean) -> Unit)? = null
    ) : BufferViewHolder(binding.root) {
      var bufferId: BufferId? = null
      var networkId: NetworkId? = null

      private var none: Int = 0
      private var activity: Int = 0
      private var message: Int = 0
      private var highlight: Int = 0

      private var expanded: Boolean = false

      init {
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

        binding.status.setOnClickListener {
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

      override fun bind(item: BufferListItem, messageSettings: MessageSettings) {
        binding.name.text = item.props.name
        bufferId = item.props.info.bufferId
        networkId = item.props.info.networkId

        binding.name.setTextColor(
          when {
            item.props.bufferActivity.hasFlag(Buffer_Activity.Highlight)     -> highlight
            item.props.bufferActivity.hasFlag(Buffer_Activity.NewMessage)    -> message
            item.props.bufferActivity.hasFlag(Buffer_Activity.OtherActivity) -> activity
            else                                                             -> none
          }
        )

        this.expanded = item.state.networkExpanded

        itemView.isSelected = item.state.selected

        if (item.state.networkExpanded) {
          binding.status.setImageResource(R.drawable.ic_chevron_up)
        } else {
          binding.status.setImageResource(R.drawable.ic_chevron_down)
        }
      }
    }

    class GroupBuffer(
      private val binding: WidgetBufferBinding,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null,
      private val dragListener: ((BufferViewHolder) -> Unit)? = null
    ) : BufferViewHolder(binding.root) {
      var bufferId: BufferId? = null

      private val online: Drawable?
      private val offline: Drawable?

      private var none: Int = 0
      private var activity: Int = 0
      private var message: Int = 0
      private var highlight: Int = 0

      init {
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

        binding.handle.setOnTouchListener { _, event ->
          if (event.action == MotionEvent.ACTION_DOWN) {
            dragListener?.invoke(this)
          }
          false
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

      override fun bind(item: BufferListItem, messageSettings: MessageSettings) {
        bufferId = item.props.info.bufferId

        binding.name.text = item.props.name
        binding.description.text = item.props.description

        binding.name.setTextColor(
          when {
            item.props.bufferActivity.hasFlag(Buffer_Activity.Highlight)     -> highlight
            item.props.bufferActivity.hasFlag(Buffer_Activity.NewMessage)    -> message
            item.props.bufferActivity.hasFlag(Buffer_Activity.OtherActivity) -> activity
            else                                                             -> none
          }
        )

        itemView.isSelected = item.state.selected

        binding.handle.visibleIf(item.state.showHandle)

        binding.description.visibleIf(item.props.description.isNotBlank())

        binding.status.setImageDrawable(
          when (item.props.bufferStatus) {
            BufferStatus.ONLINE -> online
            else                -> offline
          }
        )
      }
    }

    class ChannelBuffer(
      private val binding: WidgetBufferBinding,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null,
      private val dragListener: ((BufferViewHolder) -> Unit)? = null
    ) : BufferViewHolder(binding.root) {
      var bufferId: BufferId? = null

      private var none: Int = 0
      private var activity: Int = 0
      private var message: Int = 0
      private var highlight: Int = 0

      init {
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

        binding.handle.setOnTouchListener { _, event ->
          if (event.action == MotionEvent.ACTION_DOWN) {
            dragListener?.invoke(this)
          }
          false
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

      override fun bind(item: BufferListItem, messageSettings: MessageSettings) {
        bufferId = item.props.info.bufferId

        binding.name.text = item.props.name
        binding.description.text = item.props.description

        binding.name.setTextColor(
          when {
            item.props.bufferActivity.hasFlag(Buffer_Activity.Highlight)     -> highlight
            item.props.bufferActivity.hasFlag(Buffer_Activity.NewMessage)    -> message
            item.props.bufferActivity.hasFlag(Buffer_Activity.OtherActivity) -> activity
            else                                                             -> none
          }
        )

        itemView.isSelected = item.state.selected

        binding.handle.visibleIf(item.state.showHandle)

        binding.description.visibleIf(item.props.description.isNotBlank())

        binding.status.setImageDrawable(item.props.fallbackDrawable)
      }
    }

    class QueryBuffer(
      private val binding: WidgetBufferBinding,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null,
      private val dragListener: ((BufferViewHolder) -> Unit)? = null
    ) : BufferViewHolder(binding.root) {
      var bufferId: BufferId? = null

      private var none: Int = 0
      private var activity: Int = 0
      private var message: Int = 0
      private var highlight: Int = 0

      init {
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

        binding.handle.setOnTouchListener { _, event ->
          if (event.action == MotionEvent.ACTION_DOWN) {
            dragListener?.invoke(this)
          }
          false
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

      override fun bind(item: BufferListItem, messageSettings: MessageSettings) {
        bufferId = item.props.info.bufferId

        binding.name.text = item.props.info.bufferName
        binding.description.text = item.props.description

        binding.name.setTextColor(
          when {
            item.props.bufferActivity.hasFlag(Buffer_Activity.Highlight)     -> highlight
            item.props.bufferActivity.hasFlag(Buffer_Activity.NewMessage)    -> message
            item.props.bufferActivity.hasFlag(Buffer_Activity.OtherActivity) -> activity
            else                                                             -> none
          }
        )

        itemView.isSelected = item.state.selected

        binding.handle.visibleIf(item.state.showHandle)

        binding.description.visibleIf(item.props.description.isNotBlank())

        binding.status.loadAvatars(item.props.avatarUrls,
                                   item.props.fallbackDrawable,
                                   crop = !messageSettings.squareAvatars)
      }
    }

    class QueryAwayBuffer(
      private val binding: WidgetBufferAwayBinding,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val longClickListener: ((BufferId) -> Unit)? = null,
      private val dragListener: ((BufferViewHolder) -> Unit)? = null
    ) : BufferViewHolder(binding.root) {
      var bufferId: BufferId? = null

      private var none: Int = 0
      private var activity: Int = 0
      private var message: Int = 0
      private var highlight: Int = 0

      init {
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

        binding.handle.setOnTouchListener { _, event ->
          if (event.action == MotionEvent.ACTION_DOWN) {
            dragListener?.invoke(this)
          }
          false
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

      override fun bind(item: BufferListItem, messageSettings: MessageSettings) {
        bufferId = item.props.info.bufferId

        binding.name.text = item.props.info.bufferName
        binding.description.text = item.props.description

        binding.name.setTextColor(
          when {
            item.props.bufferActivity.hasFlag(Buffer_Activity.Highlight)     -> highlight
            item.props.bufferActivity.hasFlag(Buffer_Activity.NewMessage)    -> message
            item.props.bufferActivity.hasFlag(Buffer_Activity.OtherActivity) -> activity
            else                                                             -> none
          }
        )

        itemView.isSelected = item.state.selected

        binding.handle.visibleIf(item.state.showHandle)

        binding.description.visibleIf(item.props.description.isNotBlank())

        binding.status.loadAvatars(item.props.avatarUrls,
                                   item.props.fallbackDrawable,
                                   crop = !messageSettings.squareAvatars)
      }
    }
  }
}
