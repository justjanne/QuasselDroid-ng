package de.kuschku.quasseldroid.ui.chat.input

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
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.nicks.NickListAdapter.Companion.VIEWTYPE_AWAY
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
import de.kuschku.quasseldroid.viewmodel.data.BufferStatus
import javax.inject.Inject

class AutoCompleteAdapter @Inject constructor(
  private val messageSettings: MessageSettings
) : ListAdapter<AutoCompleteItem, AutoCompleteAdapter.AutoCompleteViewHolder>(
  object : DiffUtil.ItemCallback<AutoCompleteItem>() {
    override fun areItemsTheSame(oldItem: AutoCompleteItem, newItem: AutoCompleteItem) =
      oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: AutoCompleteItem, newItem: AutoCompleteItem) =
      oldItem == newItem
  }) {
  private var clickListener: ((String) -> Unit)? = null

  fun setOnClickListener(listener: ((String) -> Unit)?) {
    this.clickListener = listener
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
    VIEWTYPE_CHANNEL                         -> AutoCompleteViewHolder.ChannelViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.widget_buffer, parent, false),
      clickListener = clickListener
    )
    VIEWTYPE_NICK_ACTIVE, VIEWTYPE_NICK_AWAY -> {
      val holder = AutoCompleteViewHolder.NickViewHolder(
        LayoutInflater.from(parent.context).inflate(
          when (viewType) {
            VIEWTYPE_AWAY -> R.layout.widget_nick_away
            else          -> R.layout.widget_nick
          }, parent, false
        ),
        clickListener = clickListener
      )

      holder.avatar.visibleIf(messageSettings.showAvatars)

      holder
    }
    else                                     -> throw IllegalArgumentException(
      "Invoked with wrong item type"
    )
  }

  override fun onBindViewHolder(holder: AutoCompleteViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun getItemViewType(position: Int) = getItem(position).let {
    when {
      it is AutoCompleteItem.ChannelItem         -> VIEWTYPE_CHANNEL
      it is AutoCompleteItem.UserItem && it.away -> VIEWTYPE_NICK_AWAY
      else                                       -> VIEWTYPE_NICK_ACTIVE
    }
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
      @BindView(R.id.avatar)
      lateinit var avatar: ImageView

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

        nick.text = SpanFormatter.format("%s%s", data.modes, data.displayNick ?: data.nick)
        realname.text = data.realname

        avatar.loadAvatars(data.avatarUrls, data.fallbackDrawable)
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

      private val online: Drawable?
      private val offline: Drawable?

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value)
        }

        online = itemView.context.getVectorDrawableCompat(R.drawable.ic_status_channel)?.mutate()
        offline = itemView.context.getVectorDrawableCompat(R.drawable.ic_status_channel_offline)?.mutate()

        itemView.context.theme.styledAttributes(
          R.attr.colorAccent, R.attr.colorAway
        ) {
          online?.tint(getColor(0, 0))
          offline?.tint(getColor(1, 0))
        }
      }

      fun bindImpl(data: AutoCompleteItem.ChannelItem) {
        value = data.name

        name.text = data.info.bufferName
        description.text = data.description

        description.visibleIf(data.description.isNotBlank())

        status.setImageDrawable(
          when (data.bufferStatus) {
            BufferStatus.ONLINE -> online
            else                -> offline
          }
        )
      }
    }
  }

  companion object {
    const val VIEWTYPE_CHANNEL = 0
    const val VIEWTYPE_NICK_ACTIVE = 1
    const val VIEWTYPE_NICK_AWAY = 2
  }
}
