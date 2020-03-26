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

package de.kuschku.quasseldroid.ui.chat.input

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kuschku.quasseldroid.databinding.*
import de.kuschku.quasseldroid.settings.MessageSettings
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
      WidgetBufferBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      clickListener = clickListener
    )
    VIEWTYPE_NICK_ACTIVE, VIEWTYPE_NICK_AWAY -> AutoCompleteViewHolder.NickViewHolder(
      WidgetNickBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      clickListener = clickListener
    )
    VIEWTYPE_NICK_AWAY                       -> AutoCompleteViewHolder.NickAwayViewHolder(
      WidgetNickAwayBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      clickListener = clickListener
    )
    VIEWTYPE_ALIAS                           -> AutoCompleteViewHolder.AliasViewHolder(
      WidgetAliasBinding.inflate(LayoutInflater.from(parent.context), parent, false),
      clickListener = clickListener
    )
    VIEWTYPE_EMOJI                           -> AutoCompleteViewHolder.EmojiViewHolder(
      WidgetEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false),
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
      it is AutoCompleteItem.EmojiItem           -> VIEWTYPE_EMOJI
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
      data is AutoCompleteItem.EmojiItem && this is EmojiViewHolder     ->
        this.bindImpl(data, messageSettings)
      else                                                              ->
        throw IllegalArgumentException("Invoked with wrong item type")
    }

    class NickViewHolder(
      private val binding: WidgetNickBinding,
      private val clickListener: ((String, String) -> Unit)? = null
    ) : AutoCompleteViewHolder(binding.root) {
      var value: AutoCompleteItem? = null

      init {
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value.name, value.suffix)
        }
      }

      fun bindImpl(data: AutoCompleteItem.UserItem, messageSettings: MessageSettings) {
        value = data

        binding.nick.text = SpanFormatter.format("%s%s", data.modes, data.displayNick ?: data.nick)
        binding.realname.text = data.realname

        binding.avatar.visibleIf(messageSettings.showAvatars)
        binding.avatar.loadAvatars(data.avatarUrls,
                                   data.fallbackDrawable,
                                   crop = !messageSettings.squareAvatars)
      }
    }

    class NickAwayViewHolder(
      private val binding: WidgetNickAwayBinding,
      private val clickListener: ((String, String) -> Unit)? = null
    ) : AutoCompleteViewHolder(binding.root) {
      var value: AutoCompleteItem? = null

      init {
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value.name, value.suffix)
        }
      }

      fun bindImpl(data: AutoCompleteItem.UserItem, messageSettings: MessageSettings) {
        value = data

        binding.nick.text = SpanFormatter.format("%s%s", data.modes, data.displayNick ?: data.nick)
        binding.realname.text = data.realname

        binding.avatar.visibleIf(messageSettings.showAvatars)
        binding.avatar.loadAvatars(data.avatarUrls,
                           data.fallbackDrawable,
                           crop = !messageSettings.squareAvatars)
      }
    }

    class ChannelViewHolder(
      private val binding: WidgetBufferBinding,
      private val clickListener: ((String, String) -> Unit)? = null
    ) : AutoCompleteViewHolder(binding.root) {
      var value: AutoCompleteItem? = null

      init {
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value.name, value.suffix)
        }
      }

      fun bindImpl(data: AutoCompleteItem.ChannelItem, messageSettings: MessageSettings) {
        value = data

        binding.name.text = data.info.bufferName
        binding.description.text = data.description

        binding.description.visibleIf(data.description.isNotBlank())

        binding.status.setImageDrawable(data.icon)
      }
    }

    class AliasViewHolder(
      private val binding: WidgetAliasBinding,
      private val clickListener: ((String, String) -> Unit)? = null
    ) : AutoCompleteViewHolder(binding.root) {
      var value: AutoCompleteItem? = null

      init {
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value.name, value.suffix)
        }
      }

      fun bindImpl(data: AutoCompleteItem.AliasItem, messageSettings: MessageSettings) {
        value = data

        binding.alias.text = data.alias
        binding.expansion.text = data.expansion
      }
    }

    class EmojiViewHolder(
      private val binding: WidgetEmojiBinding,
      private val clickListener: ((String, String) -> Unit)? = null
    ) : AutoCompleteViewHolder(binding.root) {
      var value: AutoCompleteItem? = null

      init {
        itemView.setOnClickListener {
          val value = value
          if (value != null)
            clickListener?.invoke(value.name, value.suffix)
        }
      }

      fun bindImpl(data: AutoCompleteItem.EmojiItem, messageSettings: MessageSettings) {
        value = data

        binding.emoji.text = data.replacement
        binding.shortCode.text = data.shortCodes.joinToString(", ")
      }
    }
  }

  companion object {
    const val VIEWTYPE_CHANNEL = 0
    const val VIEWTYPE_NICK_ACTIVE = 1
    const val VIEWTYPE_NICK_AWAY = 2
    const val VIEWTYPE_ALIAS = 3
    const val VIEWTYPE_EMOJI = 4
  }
}
