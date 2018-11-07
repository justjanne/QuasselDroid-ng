/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
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

package de.kuschku.quasseldroid.ui.chat.input

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
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.nicks.NickListAdapter.Companion.VIEWTYPE_AWAY
import de.kuschku.quasseldroid.util.helper.loadAvatars
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem
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
  private var clickListener: ((String, String) -> Unit)? = null

  fun setOnClickListener(listener: ((String, String) -> Unit)?) {
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
    VIEWTYPE_ALIAS                           -> AutoCompleteViewHolder.AliasViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.widget_alias, parent, false),
      clickListener = clickListener
    )
    else                                     -> throw IllegalArgumentException(
      "Invoked with wrong item type"
    )
  }

  override fun onBindViewHolder(holder: AutoCompleteViewHolder, position: Int) =
    holder.bind(getItem(position), messageSettings)

  override fun getItemViewType(position: Int) = getItem(position).let {
    when {
      it is AutoCompleteItem.ChannelItem         -> VIEWTYPE_CHANNEL
      it is AutoCompleteItem.AliasItem           -> VIEWTYPE_ALIAS
      it is AutoCompleteItem.UserItem && it.away -> VIEWTYPE_NICK_AWAY
      else                                       -> VIEWTYPE_NICK_ACTIVE
    }
  }

  sealed class AutoCompleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(data: AutoCompleteItem, messageSettings: MessageSettings) = when {
      data is AutoCompleteItem.UserItem && this is NickViewHolder       ->
        this.bindImpl(data, messageSettings)
      data is AutoCompleteItem.ChannelItem && this is ChannelViewHolder ->
        this.bindImpl(data, messageSettings)
      data is AutoCompleteItem.AliasItem && this is AliasViewHolder     ->
        this.bindImpl(data, messageSettings)
      else                                                              ->
        throw IllegalArgumentException("Invoked with wrong item type")
    }

    class NickViewHolder(
      itemView: View,
      private val clickListener: ((String, String) -> Unit)? = null
    ) : AutoCompleteViewHolder(itemView) {
      @BindView(R.id.avatar)
      lateinit var avatar: ImageView

      @BindView(R.id.nick)
      lateinit var nick: TextView

      @BindView(R.id.realname)
      lateinit var realname: TextView

      var value: AutoCompleteItem? = null

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value.name, value.suffix)
        }
      }

      fun bindImpl(data: AutoCompleteItem.UserItem, messageSettings: MessageSettings) {
        value = data

        nick.text = SpanFormatter.format("%s%s", data.modes, data.displayNick ?: data.nick)
        realname.text = data.realname

        avatar.loadAvatars(data.avatarUrls,
                           data.fallbackDrawable,
                           crop = !messageSettings.squareAvatars)
      }
    }

    class ChannelViewHolder(
      itemView: View,
      private val clickListener: ((String, String) -> Unit)? = null
    ) : AutoCompleteViewHolder(itemView) {
      @BindView(R.id.status)
      lateinit var status: ImageView

      @BindView(R.id.name)
      lateinit var name: TextView

      @BindView(R.id.description)
      lateinit var description: TextView

      var value: AutoCompleteItem? = null

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value.name, value.suffix)
        }
      }

      fun bindImpl(data: AutoCompleteItem.ChannelItem, messageSettings: MessageSettings) {
        value = data

        name.text = data.info.bufferName
        description.text = data.description

        description.visibleIf(data.description.isNotBlank())

        status.setImageDrawable(data.icon)
      }
    }

    class AliasViewHolder(
      itemView: View,
      private val clickListener: ((String, String) -> Unit)? = null
    ) : AutoCompleteViewHolder(itemView) {
      @BindView(R.id.alias)
      lateinit var alias: TextView

      @BindView(R.id.expansion)
      lateinit var expansion: TextView

      var value: AutoCompleteItem? = null

      init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value.name, value.suffix)
        }
      }

      fun bindImpl(data: AutoCompleteItem.AliasItem, messageSettings: MessageSettings) {
        value = data

        alias.text = data.alias
        expansion.text = data.expansion
      }
    }
  }

  companion object {
    const val VIEWTYPE_CHANNEL = 0
    const val VIEWTYPE_NICK_ACTIVE = 1
    const val VIEWTYPE_NICK_AWAY = 2
    const val VIEWTYPE_ALIAS = 3
  }
}
