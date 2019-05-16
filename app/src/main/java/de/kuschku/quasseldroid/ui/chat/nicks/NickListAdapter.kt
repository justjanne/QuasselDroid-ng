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

package de.kuschku.quasseldroid.ui.chat.nicks

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
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.util.helper.nullIf
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.letIf
import de.kuschku.quasseldroid.util.helper.loadAvatars
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import de.kuschku.quasseldroid.util.ui.fastscroll.views.FastScrollRecyclerView
import de.kuschku.quasseldroid.viewmodel.data.IrcUserItem

class NickListAdapter(
  private val messageSettings: MessageSettings,
  private val clickListener: ((NetworkId, String) -> Unit)? = null
) : ListAdapter<IrcUserItem, NickListAdapter.NickViewHolder>(
  object : DiffUtil.ItemCallback<IrcUserItem>() {
    override fun areItemsTheSame(oldItem: IrcUserItem, newItem: IrcUserItem) =
      oldItem.nick == newItem.nick

    override fun areContentsTheSame(oldItem: IrcUserItem, newItem: IrcUserItem) =
      oldItem == newItem
  }), FastScrollRecyclerView.SectionedAdapter {
  override fun getSectionName(position: Int) = getItem(position).let {
    it.modes.letIf(it.modes.isNotEmpty()) { it.substring(0, 1) } +
    (it.initial.nullIf(String?::isNullOrBlank) ?: "123")
  }

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
    holder.bind(getItem(position), messageSettings)

  override fun getItemViewType(position: Int) = if (getItem(position).away) {
    VIEWTYPE_AWAY
  } else {
    VIEWTYPE_ACTIVE
  }

  class NickViewHolder(
    itemView: View,
    private val clickListener: ((NetworkId, String) -> Unit)? = null
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.avatar)
    lateinit var avatar: ImageView

    @BindView(R.id.nick)
    lateinit var nick: TextView

    @BindView(R.id.realname)
    lateinit var realname: TextView

    var user: IrcUserItem? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        val nick = user
        if (nick != null)
          clickListener?.invoke(nick.networkId, nick.nick)
      }
    }

    fun bind(data: IrcUserItem, messageSettings: MessageSettings) {
      user = data

      nick.text = SpanFormatter.format("%s%s", data.modes, data.displayNick ?: data.nick)
      realname.text = data.realname

      avatar.loadAvatars(data.avatarUrls,
                         data.fallbackDrawable,
                         crop = !messageSettings.squareAvatars)
    }
  }

  companion object {
    const val VIEWTYPE_ACTIVE = 0
    const val VIEWTYPE_AWAY = 1
  }
}
