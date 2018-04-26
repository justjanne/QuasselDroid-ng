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

package de.kuschku.quasseldroid.ui.chat.messages

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.util.helper.getOrPut
import de.kuschku.quasseldroid.util.helper.loadAvatars
import de.kuschku.quasseldroid.util.ui.DoubleClickHelper
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject

class MessageAdapter @Inject constructor(
  private val messageRenderer: MessageRenderer
) : PagedListAdapter<DisplayMessage, MessageAdapter.QuasselMessageViewHolder>(
  object : DiffUtil.ItemCallback<DisplayMessage>() {
    override fun areItemsTheSame(oldItem: DisplayMessage, newItem: DisplayMessage) =
      oldItem.content.messageId == newItem.content.messageId

    override fun areContentsTheSame(oldItem: DisplayMessage, newItem: DisplayMessage) =
      oldItem == newItem
  }) {
  private val movementMethod = BetterLinkMovementMethod.newInstance()
  private var clickListener: ((FormattedMessage) -> Unit)? = null
  private var longClickListener: ((FormattedMessage) -> Unit)? = null
  private var doubleClickListener: ((QuasselDatabase.DatabaseMessage) -> Unit)? = null
  private var expansionListener: ((QuasselDatabase.DatabaseMessage) -> Unit)? = null
  private var urlLongClickListener: ((TextView, String) -> Boolean)? = null

  fun setOnClickListener(listener: ((FormattedMessage) -> Unit)?) {
    this.clickListener = listener
  }

  fun setOnLongClickListener(listener: ((FormattedMessage) -> Unit)?) {
    this.longClickListener = listener
  }

  fun setOnDoubleClickListener(listener: ((QuasselDatabase.DatabaseMessage) -> Unit)?) {
    this.doubleClickListener = listener
  }

  fun setOnExpansionListener(listener: ((QuasselDatabase.DatabaseMessage) -> Unit)?) {
    this.expansionListener = listener
  }

  fun setOnUrlLongClickListener(listener: ((TextView, String) -> Boolean)?) {
    this.urlLongClickListener = listener
  }

  init {
    movementMethod.setOnLinkLongClickListener { textView, url ->
      urlLongClickListener?.invoke(textView, url) ?: false
    }
  }

  private val messageCache = LruCache<DisplayMessage.Tag, FormattedMessage>(512)

  fun clearCache() {
    messageCache.evictAll()
  }

  override fun onBindViewHolder(holder: QuasselMessageViewHolder, position: Int) {
    getItem(position)?.let {
      messageRenderer.bind(
        holder,
        messageCache.getOrPut(it.tag) {
          messageRenderer.render(holder.itemView.context, it)
        },
        it.content
      )
    }
  }

  override fun getItemViewType(position: Int) = getItem(position)?.let {
    Message_Flag.of(it.content.type).value or
      (if (Message_Flag.of(it.content.flag).hasFlag(Message_Flag.Highlight)) MASK_HIGHLIGHT else 0x00) or
      (if (it.isFollowUp) MASK_FOLLOWUP else 0x00) or
      (if (it.isEmoji) MASK_EMOJI else 0x00)
  } ?: 0

  override fun getItemId(position: Int): Long {
    return getItem(position)?.content?.messageId?.toLong() ?: 0L
  }

  private fun messageType(viewType: Int): Message_Type? =
    Message_Type.of(viewType and MASK_TYPE).enabledValues().firstOrNull()

  private fun hasHiglight(viewType: Int) = viewType and MASK_HIGHLIGHT != 0

  private fun isFollowUp(viewType: Int) = viewType and MASK_FOLLOWUP != 0

  private fun isEmoji(viewType: Int) = viewType and MASK_EMOJI != 0

  companion object {
    private const val SHIFT_HIGHLIGHT = 32 - 1
    private const val SHIFT_FOLLOWUP = SHIFT_HIGHLIGHT - 1
    private const val SHIFT_EMOJI = SHIFT_FOLLOWUP - 1
    const val MASK_HIGHLIGHT = 0x01 shl SHIFT_HIGHLIGHT
    const val MASK_FOLLOWUP = 0x01 shl SHIFT_FOLLOWUP
    const val MASK_EMOJI = 0x01 shl SHIFT_EMOJI
    const val MASK_TYPE = 0xFFFFFF
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuasselMessageViewHolder {
    val messageType = messageType(viewType)
    val hasHighlight = hasHiglight(viewType)
    val isFollowUp = isFollowUp(viewType)
    val isEmoji = isEmoji(viewType)
    val viewHolder = QuasselMessageViewHolder(
      LayoutInflater.from(parent.context).inflate(
        messageRenderer.layout(messageType, hasHighlight, isFollowUp, isEmoji),
        parent,
        false
      ),
      clickListener,
      longClickListener,
      doubleClickListener,
      expansionListener,
      movementMethod
    )
    messageRenderer.init(viewHolder, messageType, hasHighlight, isFollowUp, isEmoji)
    return viewHolder
  }

  operator fun get(position: Int) = if (position in 0 until itemCount) {
    getItem(position)
  } else {
    null
  }

  class QuasselMessageViewHolder(
    itemView: View,
    clickListener: ((FormattedMessage) -> Unit)? = null,
    longClickListener: ((FormattedMessage) -> Unit)? = null,
    doubleClickListener: ((QuasselDatabase.DatabaseMessage) -> Unit)? = null,
    expansionListener: ((QuasselDatabase.DatabaseMessage) -> Unit)? = null,
    movementMethod: BetterLinkMovementMethod
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.time_left)
    @JvmField
    var timeLeft: TextView? = null

    @BindView(R.id.time_right)
    @JvmField
    var timeRight: TextView? = null

    @BindView(R.id.avatar)
    @JvmField
    var avatar: ImageView? = null

    @BindView(R.id.name)
    @JvmField
    var name: TextView? = null

    @BindView(R.id.realname)
    @JvmField
    var realname: TextView? = null

    @BindView(R.id.content)
    @JvmField
    var content: TextView? = null

    @BindView(R.id.combined)
    @JvmField
    var combined: TextView? = null

    private var message: FormattedMessage? = null
    private var original: QuasselDatabase.DatabaseMessage? = null
    private var selectable: Boolean = false
    private var clickable: Boolean = false

    private val localClickListener = View.OnClickListener {
      if (clickable) {
        message?.let {
          clickListener?.invoke(it)
        }
      }
    }

    private val localLongClickListener = View.OnLongClickListener {
      if (selectable) {
        message?.let {
          longClickListener?.invoke(it)
        }
      }
      true
    }

    private val localDoubleClickListener = {
      if (clickable) {
        original?.let {
          doubleClickListener?.invoke(it)
        }
      }
    }

    init {
      ButterKnife.bind(this, itemView)
      content?.movementMethod = movementMethod
      combined?.movementMethod = movementMethod

      itemView.setOnClickListener(localClickListener)
      itemView.setOnLongClickListener(localLongClickListener)
      itemView.setOnTouchListener(DoubleClickHelper(itemView).apply {
        this.doubleClickListener = localDoubleClickListener
      })
    }

    fun bind(message: FormattedMessage, original: QuasselDatabase.DatabaseMessage,
             selectable: Boolean = true, clickable: Boolean = true) {
      this.message = message
      this.original = original
      this.selectable = selectable
      this.clickable = clickable

      timeLeft?.text = message.time
      timeRight?.text = message.time
      name?.text = message.name
      realname?.text = message.realName
      content?.text = message.content
      combined?.text = message.combined

      this.itemView.isSelected = message.isSelected

      avatar?.loadAvatars(message.avatarUrls, message.fallbackDrawable)
    }
  }
}
