/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat.nicks

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
import de.kuschku.quasseldroid.util.helper.loadAvatars
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import de.kuschku.quasseldroid.viewmodel.data.IrcUserItem

class NickListAdapter(
  private val messageSettings: MessageSettings,
  private val clickListener: ((String) -> Unit)? = null
) : ListAdapter<IrcUserItem, NickListAdapter.NickViewHolder>(
  object : DiffUtil.ItemCallback<IrcUserItem>() {
    override fun areItemsTheSame(oldItem: IrcUserItem, newItem: IrcUserItem) =
      oldItem.nick == newItem.nick

    override fun areContentsTheSame(oldItem: IrcUserItem?, newItem: IrcUserItem?) =
      oldItem == newItem
  }) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NickViewHolder {
    val holder = NickViewHolder(
      LayoutInflater.from(parent.context).inflate(
        when (viewType) {
          VIEWTYPE_AWAY -> R.layout.widget_nick_away
          else          -> R.layout.widget_nick
        }, parent, false
      ),
      clickListener = clickListener
    )

    holder.avatar.visibleIf(messageSettings.showAvatars)

    return holder
  }

  operator fun get(position: Int): IrcUserItem? = super.getItem(position)

  override fun onBindViewHolder(holder: NickViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun getItemViewType(position: Int) = if (getItem(position).away) {
    VIEWTYPE_AWAY
  } else {
    VIEWTYPE_ACTIVE
  }

  class NickViewHolder(
    itemView: View,
    private val clickListener: ((String) -> Unit)? = null
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.avatar)
    lateinit var avatar: ImageView

    @BindView(R.id.nick)
    lateinit var nick: TextView

    @BindView(R.id.realname)
    lateinit var realname: TextView

    var user: String? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        val nick = user
        if (nick != null)
          clickListener?.invoke(nick)
      }
    }

    fun bind(data: IrcUserItem) {
      user = data.nick

      nick.text = SpanFormatter.format("%s%s", data.modes, data.displayNick ?: data.nick)
      realname.text = data.realname

      avatar.loadAvatars(data.avatarUrls, data.fallbackDrawable)
    }
  }

  companion object {
    const val VIEWTYPE_ACTIVE = 0
    const val VIEWTYPE_AWAY = 1
  }
}
