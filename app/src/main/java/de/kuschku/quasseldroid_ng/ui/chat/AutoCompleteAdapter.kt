package de.kuschku.quasseldroid_ng.ui.chat

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
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.chat.NickListAdapter.Companion.VIEWTYPE_AWAY
import de.kuschku.quasseldroid_ng.ui.chat.buffers.BufferListAdapter
import de.kuschku.quasseldroid_ng.util.helper.getCompatDrawable
import de.kuschku.quasseldroid_ng.util.helper.styledAttributes
import de.kuschku.quasseldroid_ng.util.helper.visibleIf

class AutoCompleteAdapter(
  lifecycleOwner: LifecycleOwner,
  liveData: LiveData<Pair<String, List<AutoCompleteItem>>?>,
  runInBackground: (() -> Unit) -> Any,
  runOnUiThread: (Runnable) -> Any,
  private val clickListener: ((String) -> Unit)? = null
) : RecyclerView.Adapter<AutoCompleteAdapter.AutoCompleteViewHolder>() {
  var data = mutableListOf<AutoCompleteItem>()

  init {
    liveData.observe(
      lifecycleOwner, Observer { it: Pair<String, List<AutoCompleteItem>>? ->
      runInBackground {
        val word = it?.first ?: ""
        val list = it?.second ?: emptyList()
        val old: List<AutoCompleteItem> = data
        val new: List<AutoCompleteItem> = if (word.length >= 3) list else emptyList()
        val result = DiffUtil.calculateDiff(
          object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
              old[oldItemPosition].name == new[newItemPosition].name

            override fun getOldListSize() = old.size
            override fun getNewListSize() = new.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
              old[oldItemPosition] == new[newItemPosition]
          }, true
        )
        runOnUiThread(Runnable {
          data.clear()
          data.addAll(new)
          result.dispatchUpdatesTo(this@AutoCompleteAdapter)
        })
      }
    })
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
    VIEWTYPE_CHANNEL                         -> AutoCompleteViewHolder.ChannelViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.widget_buffer, parent, false),
      clickListener = clickListener
    )
    VIEWTYPE_NICK_ACTIVE, VIEWTYPE_NICK_AWAY -> AutoCompleteViewHolder.NickViewHolder(
      LayoutInflater.from(parent.context).inflate(
        when (viewType) {
          VIEWTYPE_AWAY -> R.layout.widget_nick_away
          else          -> R.layout.widget_nick
        }, parent, false
      ),
      clickListener = clickListener
    )
    else                                     -> throw IllegalArgumentException(
      "Invoked with wrong item type"
    )
  }

  override fun onBindViewHolder(holder: AutoCompleteViewHolder, position: Int) =
    holder.bind(data[position])

  override fun getItemCount() = data.size

  override fun getItemViewType(position: Int) = data[position].let { it ->
    when {
      it is AutoCompleteItem.ChannelItem         -> VIEWTYPE_CHANNEL
      it is AutoCompleteItem.UserItem && it.away -> VIEWTYPE_NICK_AWAY
      else                                       -> VIEWTYPE_NICK_ACTIVE
    }
  }

  sealed class AutoCompleteItem(open val name: String) : Comparable<AutoCompleteItem> {
    override fun compareTo(other: AutoCompleteItem): Int {
      return when {
        this is AutoCompleteItem.UserItem &&
        other is AutoCompleteItem.ChannelItem -> -1
        this is AutoCompleteItem.ChannelItem &&
        other is AutoCompleteItem.UserItem    -> 1
        else                                  -> this.name.compareTo(other.name)
      }
    }

    data class UserItem(
      val nick: String,
      val modes: String,
      val lowestMode: Int,
      val realname: CharSequence,
      val away: Boolean,
      val networkCasemapping: String
    ) : AutoCompleteItem(nick)

    data class ChannelItem(
      val info: BufferInfo,
      val network: INetwork.NetworkInfo,
      val bufferStatus: BufferListAdapter.BufferStatus,
      val description: CharSequence
    ) : AutoCompleteItem(info.bufferName ?: "")
  }

  sealed class AutoCompleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(data: AutoCompleteItem) = when {
      data is AutoCompleteItem.UserItem && this is NickViewHolder       -> this.bindImpl(data)
      data is AutoCompleteItem.ChannelItem && this is ChannelViewHolder -> this.bindImpl(data)
      else                                                              -> throw IllegalArgumentException(
        "Invoked with wrong item type"
      )
    }

    class NickViewHolder(
      itemView: View,
      private val clickListener: ((String) -> Unit)? = null
    ) : AutoCompleteViewHolder(itemView) {
      @BindView(R.id.modesContainer)
      lateinit var modesContainer: View

      @BindView(R.id.modes)
      lateinit var modes: TextView

      @BindView(R.id.nick)
      lateinit var nick: TextView

      @BindView(R.id.realname)
      lateinit var realname: TextView

      var value: String? = null

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value)
        }
      }

      fun bindImpl(data: AutoCompleteItem.UserItem) {
        value = data.name

        nick.text = data.nick
        modes.text = data.modes
        realname.text = data.realname

        modes.visibleIf(data.modes.isNotBlank())
      }
    }

    class ChannelViewHolder(
      itemView: View,
      private val clickListener: ((String) -> Unit)? = null
    ) : AutoCompleteViewHolder(itemView) {
      @BindView(R.id.status)
      lateinit var status: ImageView

      @BindView(R.id.name)
      lateinit var name: TextView

      @BindView(R.id.description)
      lateinit var description: TextView

      var value: String? = null

      private val online: Drawable
      private val offline: Drawable

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value)
        }

        online = itemView.context.getCompatDrawable(R.drawable.ic_status_channel).mutate()
        offline = itemView.context.getCompatDrawable(R.drawable.ic_status_channel_offline).mutate()

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway
        ) {
          DrawableCompat.setTint(online, getColor(0, 0))
          DrawableCompat.setTint(offline, getColor(1, 0))
        }
      }

      fun bindImpl(data: AutoCompleteItem.ChannelItem) {
        value = data.name

        name.text = data.info.bufferName
        description.text = data.description

        description.visibleIf(data.description.isNotBlank())

        status.setImageDrawable(
          when (data.bufferStatus) {
            BufferListAdapter.BufferStatus.ONLINE -> online
            else                                  -> offline
          }
        )
      }
    }
  }

  companion object {
    val VIEWTYPE_CHANNEL = 0
    val VIEWTYPE_NICK_ACTIVE = 1
    val VIEWTYPE_NICK_AWAY = 2
  }
}