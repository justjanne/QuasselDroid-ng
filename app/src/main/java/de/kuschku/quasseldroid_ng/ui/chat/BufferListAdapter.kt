package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
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
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.getCompatDrawable
import de.kuschku.quasseldroid_ng.util.helper.styledAttributes
import de.kuschku.quasseldroid_ng.util.helper.zip

class BufferListAdapter(
  lifecycleOwner: LifecycleOwner,
  liveData: LiveData<List<BufferProps>?>,
  runInBackground: (() -> Unit) -> Any,
  runOnUiThread: (Runnable) -> Any,
  private val clickListener: ((BufferId) -> Unit)? = null
) : RecyclerView.Adapter<BufferListAdapter.BufferViewHolder>() {
  var data = mutableListOf<BufferListItem>()

  var collapsedNetworks = MutableLiveData<Set<NetworkId>>()

  fun expandListener(networkId: NetworkId) {
    if (collapsedNetworks.value.orEmpty().contains(networkId))
      collapsedNetworks.postValue(collapsedNetworks.value.orEmpty() - networkId)
    else
      collapsedNetworks.postValue(collapsedNetworks.value.orEmpty() + networkId)
  }

  init {
    collapsedNetworks.value = emptySet()

    liveData.zip(collapsedNetworks).observe(
      lifecycleOwner, Observer { it: Pair<List<BufferProps>?, Set<NetworkId>>? ->
      runInBackground {
        val list = it?.first ?: emptyList()
        val collapsedNetworks = it?.second ?: emptySet()

        val old: List<BufferListItem> = data
        val new: List<BufferListItem> = list.sortedBy { props ->
          props.network.networkName
        }.map { props ->
          BufferListItem(
            props,
            BufferState(
              networkExpanded = !collapsedNetworks.contains(props.network.networkId)
            )
          )
        }.filter { (props, state) ->
          props.info.type.hasFlag(BufferInfo.Type.StatusBuffer) || state.networkExpanded
        }

        val result = DiffUtil.calculateDiff(
          object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
              = old[oldItemPosition].props.info.bufferId == new[newItemPosition].props.info.bufferId

            override fun getOldListSize() = old.size
            override fun getNewListSize() = new.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
              = old[oldItemPosition] == new[newItemPosition]
          }, true
        )
        runOnUiThread(
          Runnable {
            data.clear()
            data.addAll(new)
            result.dispatchUpdatesTo(this@BufferListAdapter)
          }
        )
      }
    }
    )
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
    BufferInfo.Type.ChannelBuffer.toInt() -> BufferViewHolder.ChannelBuffer(
      LayoutInflater.from(parent.context).inflate(
        R.layout.widget_buffer, parent, false
      ),
      clickListener = clickListener
    )
    BufferInfo.Type.QueryBuffer.toInt()   -> BufferViewHolder.QueryBuffer(
      LayoutInflater.from(parent.context).inflate(
        R.layout.widget_buffer, parent, false
      ),
      clickListener = clickListener
    )
    BufferInfo.Type.GroupBuffer.toInt()   -> BufferViewHolder.GroupBuffer(
      LayoutInflater.from(parent.context).inflate(
        R.layout.widget_buffer, parent, false
      ),
      clickListener = clickListener
    )
    BufferInfo.Type.StatusBuffer.toInt()  -> BufferViewHolder.StatusBuffer(
      LayoutInflater.from(parent.context).inflate(
        R.layout.widget_network, parent, false
      ),
      clickListener = clickListener,
      expansionListener = ::expandListener
    )
    else                                  -> throw IllegalArgumentException(
      "No such viewType: $viewType"
    )
  }

  override fun onBindViewHolder(holder: BufferViewHolder, position: Int)
    = holder.bind(data[position].props, data[position].state)

  override fun getItemCount() = data.size

  override fun getItemViewType(position: Int) = data[position].props.info.type.toInt()

  data class BufferListItem(
    val props: BufferProps,
    val state: BufferState
  )

  data class BufferProps(
    val info: BufferInfo,
    val network: INetwork.NetworkInfo,
    val bufferStatus: BufferStatus,
    val description: String,
    val activity: Buffer_Activity,
    val highlights: Int = 0
  )

  data class BufferState(
    val networkExpanded: Boolean
  )

  abstract class BufferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(props: BufferProps, state: BufferState)

    fun <T> status(target: T, actual: T) = if (target == actual) {
      View.VISIBLE
    } else {
      View.GONE
    }

    class StatusBuffer(
      itemView: View,
      private val clickListener: ((BufferId) -> Unit)? = null,
      private val expansionListener: ((NetworkId) -> Unit)? = null
    ) : BufferViewHolder(itemView) {
      @BindView(R.id.status)
      lateinit var status: ImageView

      @BindView(R.id.name)
      lateinit var name: TextView

      var bufferId: BufferId? = null
      var networkId: NetworkId? = null

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val buffer = bufferId
          if (buffer != null)
            clickListener?.invoke(buffer)
        }

        status.setOnClickListener {
          val network = networkId
          if (network != null)
            expansionListener?.invoke(network)
        }
      }

      override fun bind(props: BufferProps, state: BufferState) {
        name.text = props.network.networkName
        bufferId = props.info.bufferId
        networkId = props.info.networkId

        if (state.networkExpanded) {
          status.setImageDrawable(itemView.context.getCompatDrawable(R.drawable.ic_chevron_up))
        } else {
          status.setImageDrawable(itemView.context.getCompatDrawable(R.drawable.ic_chevron_down))
        }
      }
    }

    class GroupBuffer(
      itemView: View,
      private val clickListener: ((BufferId) -> Unit)? = null
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

        online = itemView.context.getCompatDrawable(R.drawable.ic_status)
        offline = itemView.context.getCompatDrawable(R.drawable.ic_status_offline)

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway,
          R.attr.colorForeground, R.attr.colorTintActivity, R.attr.colorTintMessage,
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
          when (props.activity) {
            Buffer_Activity.NoActivity    -> none
            Buffer_Activity.OtherActivity -> activity
            Buffer_Activity.NewMessage    -> message
            Buffer_Activity.Highlight     -> highlight
          }
        )

        description.visibility = if (props.description == "") {
          View.GONE
        } else {
          View.VISIBLE
        }

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
      private val clickListener: ((BufferId) -> Unit)? = null
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

        online = itemView.context.getCompatDrawable(R.drawable.ic_status_channel)
        offline = itemView.context.getCompatDrawable(R.drawable.ic_status_channel_offline)

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway,
          R.attr.colorForeground, R.attr.colorTintActivity, R.attr.colorTintMessage,
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
          when (props.activity) {
            Buffer_Activity.NoActivity    -> none
            Buffer_Activity.OtherActivity -> activity
            Buffer_Activity.NewMessage    -> message
            Buffer_Activity.Highlight     -> highlight
          }
        )

        description.visibility = if (props.description == "") {
          View.GONE
        } else {
          View.VISIBLE
        }

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
      private val clickListener: ((BufferId) -> Unit)? = null
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

        online = itemView.context.getCompatDrawable(R.drawable.ic_status)
        away = itemView.context.getCompatDrawable(R.drawable.ic_status)
        offline = itemView.context.getCompatDrawable(R.drawable.ic_status_offline)

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway,
          R.attr.colorForeground, R.attr.colorTintActivity, R.attr.colorTintMessage,
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
          when (props.activity) {
            Buffer_Activity.NoActivity    -> none
            Buffer_Activity.OtherActivity -> activity
            Buffer_Activity.NewMessage    -> message
            Buffer_Activity.Highlight     -> highlight
          }
        )

        description.visibility = if (props.description == "") {
          View.GONE
        } else {
          View.VISIBLE
        }

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

  enum class BufferStatus {
    ONLINE,
    AWAY,
    OFFLINE
  }
}
