package de.kuschku.quasseldroid.ui.chat.messages

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.request.RequestOptions
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Flags
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.Message_Types
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.util.helper.getOrPut
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class MessageAdapter(
  private val messageRenderer: MessageRenderer,
  private val clickListener: ((FormattedMessage) -> Unit)? = null,
  private val selectionListener: ((FormattedMessage) -> Unit)? = null,
  private val expansionListener: ((QuasselDatabase.DatabaseMessage) -> Unit)? = null
) : PagedListAdapter<DisplayMessage, MessageAdapter.QuasselMessageViewHolder>(
  object : DiffUtil.ItemCallback<DisplayMessage>() {
    override fun areItemsTheSame(oldItem: DisplayMessage, newItem: DisplayMessage) =
      oldItem.content.messageId == newItem.content.messageId

    override fun areContentsTheSame(oldItem: DisplayMessage, newItem: DisplayMessage) =
      oldItem == newItem
  }) {

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
    viewType(Message_Flags.of(it.content.type),
             Message_Flags.of(it.content.flag),
             it.content.followUp)
  } ?: 0

  private fun viewType(type: Message_Types, flags: Message_Flags, followUp: Boolean) =
    type.value or
      (if (flags.hasFlag(Message_Flag.Highlight)) MASK_HIGHLIGHT else 0x00) or
      (if (followUp) MASK_FOLLOWUP else 0x00)

  override fun getItemId(position: Int): Long {
    return getItem(position)?.content?.messageId?.toLong() ?: 0L
  }

  private fun messageType(viewType: Int): Message_Type? =
    Message_Type.of(viewType and MASK_TYPE).enabledValues().firstOrNull()

  private fun hasHiglight(viewType: Int) = viewType and MASK_HIGHLIGHT != 0

  private fun isFollowUp(viewType: Int) = viewType and MASK_FOLLOWUP != 0

  companion object {
    const val SHIFT_HIGHLIGHT = 32 - 1
    const val SHIFT_FOLLOWUP = SHIFT_HIGHLIGHT - 1
    const val MASK_HIGHLIGHT = 0x01 shl SHIFT_HIGHLIGHT
    const val MASK_FOLLOWUP = 0x01 shl SHIFT_FOLLOWUP
    const val MASK_TYPE = 0xFFFFFF
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuasselMessageViewHolder {
    val messageType = messageType(viewType)
    val hasHighlight = hasHiglight(viewType)
    val isFollowUp = isFollowUp(viewType)
    val viewHolder = QuasselMessageViewHolder(
      LayoutInflater.from(parent.context).inflate(
        messageRenderer.layout(messageType, hasHighlight, isFollowUp),
        parent,
        false
      ),
      clickListener,
      selectionListener,
      expansionListener
    )
    messageRenderer.init(viewHolder, messageType, hasHighlight, isFollowUp)
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
    selectionListener: ((FormattedMessage) -> Unit)? = null,
    expansionListener: ((QuasselDatabase.DatabaseMessage) -> Unit)? = null
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

    @BindView(R.id.avatar_container)
    @JvmField
    var avatarContainer: View? = null

    @BindView(R.id.avatar_placeholder)
    @JvmField
    var avatarPlaceholder: Space? = null

    @BindView(R.id.name)
    @JvmField
    var name: TextView? = null

    @BindView(R.id.content)
    @JvmField
    var content: TextView? = null

    @BindView(R.id.combined)
    @JvmField
    var combined: TextView? = null

    @BindView(R.id.markerline)
    @JvmField
    var markerline: View? = null

    private var message: FormattedMessage? = null
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
          selectionListener?.invoke(it)
        }
      }
      true
    }

    init {
      ButterKnife.bind(this, itemView)

      content?.movementMethod = BetterLinkMovementMethod.getInstance()
      combined?.movementMethod = BetterLinkMovementMethod.getInstance()

      itemView.setOnClickListener(localClickListener)
      itemView.setOnLongClickListener(localLongClickListener)
    }

    fun bind(message: FormattedMessage, selectable: Boolean = true, clickable: Boolean = true) {
      this.message = message
      this.selectable = selectable
      this.clickable = clickable

      timeLeft?.text = message.time
      timeRight?.text = message.time
      name?.text = message.name
      content?.text = message.content
      combined?.text = message.combined
      markerline?.visibleIf(message.isMarkerLine)

      this.itemView.isSelected = message.isSelected

      avatar?.let { avatarView ->
        GlideApp.with(itemView)
          .load(message.avatarUrl)
          .apply(RequestOptions.circleCropTransform())
          .placeholder(message.fallbackDrawable)
          .into(avatarView)
      }
    }
  }
}

