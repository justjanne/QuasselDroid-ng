package de.kuschku.quasseldroid.ui.chat.buffers

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat
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
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.viewmodel.data.BufferListItem
import de.kuschku.quasseldroid.viewmodel.data.BufferProps
import de.kuschku.quasseldroid.viewmodel.data.BufferState
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import io.reactivex.subjects.BehaviorSubject

class BufferListAdapter(
  lifecycleOwner: LifecycleOwner,
  liveData: LiveData<List<BufferProps>?>,
  private val selectedBuffer: BehaviorSubject<BufferId>,
  private val collapsedNetworks: BehaviorSubject<Set<NetworkId>>,
  runInBackground: (() -> Unit) -> Any,
  runOnUiThread: (Runnable) -> Any,
  private val clickListener: ((BufferId) -> Unit)? = null,
  private val longClickListener: ((BufferId) -> Unit)? = null
) : RecyclerView.Adapter<BufferListAdapter.BufferViewHolder>() {
  var data = mutableListOf<BufferListItem>()

  fun expandListener(networkId: NetworkId) {
    if (collapsedNetworks.value.orEmpty().contains(networkId))
      collapsedNetworks.onNext(collapsedNetworks.value.orEmpty() - networkId)
    else
      collapsedNetworks.onNext(collapsedNetworks.value.orEmpty() + networkId)
  }

  fun toggleSelection(buffer: BufferId) {
    if (selectedBuffer.value == buffer) {
      selectedBuffer.onNext(-1)
    } else {
      selectedBuffer.onNext(buffer)
    }
  }

  fun unselectAll() {
    selectedBuffer.onNext(-1)
  }

  init {
    liveData.zip(collapsedNetworks.toLiveData(), selectedBuffer.toLiveData()).observe(
      lifecycleOwner, Observer { it: Triple<List<BufferProps>?, Set<NetworkId>, BufferId>? ->
      runInBackground {
        val list = it?.first ?: emptyList()
        val collapsedNetworks = it?.second ?: emptySet()
        val selected = it?.third ?: -1

        val old: List<BufferListItem> = data
        val new: List<BufferListItem> = list.sortedBy { props ->
          !props.info.type.hasFlag(Buffer_Type.StatusBuffer)
        }.sortedBy { props ->
          props.network.networkName
        }.map { props ->
          BufferListItem(
            props,
            BufferState(
              networkExpanded = !collapsedNetworks.contains(props.network.networkId),
              selected = selected == props.info.bufferId
            )
          )
        }.filter { (props, state) ->
          props.info.type.hasFlag(BufferInfo.Type.StatusBuffer) || state.networkExpanded
        }

        val result = DiffUtil.calculateDiff(
          object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
              old[oldItemPosition].props.info.bufferId == new[newItemPosition].props.info.bufferId

            override fun getOldListSize() = old.size
            override fun getNewListSize() = new.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
              old[oldItemPosition] == new[newItemPosition]
          }, true
        )
        runOnUiThread(
          Runnable {
            data.clear()
            data.addAll(new)
            result.dispatchUpdatesTo(this@BufferListAdapter)
          })
      }
    })
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
    holder.bind(data[position].props, data[position].state)

  override fun getItemCount() = data.size

  override fun getItemViewType(position: Int) = data[position].props.info.type.toInt()

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
          status.setImageDrawable(itemView.context.getCompatDrawable(R.drawable.ic_chevron_up))
        } else {
          status.setImageDrawable(itemView.context.getCompatDrawable(R.drawable.ic_chevron_down))
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

      private val online: Drawable
      private val offline: Drawable

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

        online = itemView.context.getCompatDrawable(R.drawable.ic_status).mutate()
        offline = itemView.context.getCompatDrawable(R.drawable.ic_status_offline).mutate()

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway,
          R.attr.colorTextPrimary, R.attr.colorTintActivity, R.attr.colorTintMessage,
          R.attr.colorTintHighlight
        ) {
          DrawableCompat.setTint(online, getColor(0, 0))
          DrawableCompat.setTint(offline, getColor(1, 0))

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

      private val online: Drawable
      private val offline: Drawable

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

        online = itemView.context.getCompatDrawable(R.drawable.ic_status_channel).mutate()
        offline = itemView.context.getCompatDrawable(R.drawable.ic_status_channel_offline).mutate()

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway,
          R.attr.colorTextPrimary, R.attr.colorTintActivity, R.attr.colorTintMessage,
          R.attr.colorTintHighlight
        ) {
          DrawableCompat.setTint(online, getColor(0, 0))
          DrawableCompat.setTint(offline, getColor(1, 0))

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

      private val online: Drawable
      private val away: Drawable
      private val offline: Drawable

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

        online = itemView.context.getCompatDrawable(R.drawable.ic_status).mutate()
        away = itemView.context.getCompatDrawable(R.drawable.ic_status).mutate()
        offline = itemView.context.getCompatDrawable(R.drawable.ic_status_offline).mutate()

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway,
          R.attr.colorTextPrimary, R.attr.colorTintActivity, R.attr.colorTintMessage,
          R.attr.colorTintHighlight
        ) {
          DrawableCompat.setTint(online, getColor(0, 0))
          DrawableCompat.setTint(away, getColor(1, 0))
          DrawableCompat.setTint(offline, getColor(1, 0))

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
