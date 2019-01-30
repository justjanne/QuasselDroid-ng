/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
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

package de.kuschku.quasseldroid.ui.chat.messages

import android.annotation.SuppressLint
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.helper.getOrPut
import de.kuschku.quasseldroid.util.helper.loadAvatars
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.ui.BetterLinkMovementMethod
import de.kuschku.quasseldroid.util.ui.DoubleClickHelper
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage
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
  private var doubleClickListener: ((QuasselDatabase.MessageData) -> Unit)? = null
  private var senderIconClickListener: ((QuasselDatabase.MessageData) -> Unit)? = null
  private var expansionListener: ((QuasselDatabase.MessageData) -> Unit)? = null
  private var urlLongClickListener: ((TextView, String) -> Boolean)? = null

  fun setOnClickListener(listener: ((FormattedMessage) -> Unit)?) {
    this.clickListener = listener
  }

  fun setOnLongClickListener(listener: ((FormattedMessage) -> Unit)?) {
    this.longClickListener = listener
  }

  fun setOnDoubleClickListener(listener: ((QuasselDatabase.MessageData) -> Unit)?) {
    this.doubleClickListener = listener
  }

  fun setOnSenderIconClickListener(listener: ((QuasselDatabase.MessageData) -> Unit)?) {
    this.senderIconClickListener = listener
  }

  fun setOnExpansionListener(listener: ((QuasselDatabase.MessageData) -> Unit)?) {
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
    it.content.type.value.toInt() or
      (if (it.content.flag.hasFlag(Message_Flag.Highlight)) MASK_HIGHLIGHT else 0x00) or
      (if (it.isFollowUp) MASK_FOLLOWUP else 0x00) or
      (if (it.isEmoji) MASK_EMOJI else 0x00) or
      (if (it.content.flag.hasFlag(Message_Flag.Self)) MASK_SELF else 0x00)
  } ?: 0

  override fun getItemId(position: Int): Long {
    return getItem(position)?.content?.messageId?.id ?: 0L
  }

  private fun messageType(viewType: Int): Message_Type? =
    Message_Type.of(viewType and MASK_TYPE).enabledValues().firstOrNull()

  private fun hasHiglight(viewType: Int) = viewType and MASK_HIGHLIGHT != 0

  private fun isFollowUp(viewType: Int) = viewType and MASK_FOLLOWUP != 0

  private fun isEmoji(viewType: Int) = viewType and MASK_EMOJI != 0

  private fun isSelf(viewType: Int) = viewType and MASK_SELF != 0

  companion object {
    private const val SHIFT_HIGHLIGHT = 32 - 1
    private const val SHIFT_FOLLOWUP = SHIFT_HIGHLIGHT - 1
    private const val SHIFT_EMOJI = SHIFT_FOLLOWUP - 1
    private const val SHIFT_SELF = SHIFT_EMOJI - 1
    const val MASK_HIGHLIGHT = 0x01 shl SHIFT_HIGHLIGHT
    const val MASK_FOLLOWUP = 0x01 shl SHIFT_FOLLOWUP
    const val MASK_EMOJI = 0x01 shl SHIFT_EMOJI
    const val MASK_SELF = 0x01 shl SHIFT_SELF
    const val MASK_TYPE = 0xFFFFFF
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuasselMessageViewHolder {
    val messageType = messageType(viewType)
    val hasHighlight = hasHiglight(viewType)
    val isFollowUp = isFollowUp(viewType)
    val isEmoji = isEmoji(viewType)
    val isSelf = isSelf(viewType)
    val viewHolder = QuasselMessageViewHolder(
      LayoutInflater.from(parent.context).inflate(
        messageRenderer.layout(messageType, hasHighlight, isFollowUp, isEmoji, isSelf),
        parent,
        false
      ),
      clickListener,
      longClickListener,
      doubleClickListener,
      senderIconClickListener,
      expansionListener,
      movementMethod
    )
    messageRenderer.init(viewHolder, messageType, hasHighlight, isFollowUp, isEmoji, isSelf)
    return viewHolder
  }

  operator fun get(position: Int) = if (position in 0 until itemCount) {
    getItem(position)
  } else {
    null
  }

  @SuppressLint("ClickableViewAccessibility")
  class QuasselMessageViewHolder(
    itemView: View,
    clickListener: ((FormattedMessage) -> Unit)? = null,
    longClickListener: ((FormattedMessage) -> Unit)? = null,
    doubleClickListener: ((QuasselDatabase.MessageData) -> Unit)? = null,
    senderIconClickListener: ((QuasselDatabase.MessageData) -> Unit)? = null,
    expansionListener: ((QuasselDatabase.MessageData) -> Unit)? = null,
    movementMethod: BetterLinkMovementMethod
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.daychange_container)
    @JvmField
    var daychangeContainer: View? = null

    @BindView(R.id.daychange)
    @JvmField
    var daychange: TextView? = null

    @BindView(R.id.message_container)
    @JvmField
    var messageContainer: View? = null

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
    private var original: QuasselDatabase.MessageData? = null

    private val localClickListener = View.OnClickListener {
      message?.let {
        clickListener?.invoke(it)
      }
    }

    private val localLongClickListener = View.OnLongClickListener {
      message?.let {
        longClickListener?.invoke(it)
      }
      true
    }

    private val localDoubleClickListener: () -> Unit = {
      original?.let {
        doubleClickListener?.invoke(it)
      }
    }

    private val localSenderIconClickListener = View.OnClickListener {
      original?.let {
        senderIconClickListener?.invoke(it)
      }
    }

    init {
      ButterKnife.bind(this, itemView)
      content?.movementMethod = movementMethod
      combined?.movementMethod = movementMethod

      messageContainer?.setOnClickListener(localClickListener)
      messageContainer?.setOnLongClickListener(localLongClickListener)
      messageContainer?.setOnTouchListener(DoubleClickHelper(itemView).apply {
        this.doubleClickListener = localDoubleClickListener
      })
      avatar?.setOnClickListener(localSenderIconClickListener)
    }

    fun bind(message: FormattedMessage, original: QuasselDatabase.MessageData,
             hasDayChange: Boolean, messageSettings: MessageSettings) {
      this.message = message
      this.original = original

      timeLeft?.text = message.time
      timeRight?.text = message.time
      name?.text = message.name
      realname?.text = message.realName
      content?.text = message.content
      combined?.text = message.combined

      this.messageContainer?.isSelected = message.isSelected

      if (hasDayChange) daychange?.text = message.dayChange
      daychangeContainer?.visibleIf(hasDayChange)

      avatar?.loadAvatars(message.avatarUrls,
                          message.fallbackDrawable,
                          crop = !messageSettings.squareAvatars)
    }
  }
}
