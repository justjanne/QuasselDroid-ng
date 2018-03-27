package de.kuschku.quasseldroid.ui.chat.messages

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Flags
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.Message_Types
import de.kuschku.libquassel.util.hasFlag
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
    viewType(Message_Flags.of(it.content.type), Message_Flags.of(it.content.flag))
  } ?: 0

  private fun viewType(type: Message_Types, flags: Message_Flags) =
    if (flags.hasFlag(Message_Flag.Highlight)) {
      -type.value
    } else {
      type.value
    }

  override fun getItemId(position: Int): Long {
    return getItem(position)?.content?.messageId?.toLong() ?: 0L
  }

  private fun messageType(viewType: Int): Message_Type? =
    Message_Type.of(Math.abs(viewType)).enabledValues().firstOrNull()

  private fun hasHiglight(viewType: Int) = viewType < 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuasselMessageViewHolder {
    val messageType = messageType(viewType)
    val hasHighlight = hasHiglight(viewType)
    val viewHolder = QuasselMessageViewHolder(
      LayoutInflater.from(parent.context).inflate(
        messageRenderer.layout(messageType, hasHighlight),
        parent,
        false
      ),
      clickListener,
      selectionListener,
      expansionListener
    )
    messageRenderer.init(viewHolder, messageType, hasHighlight)
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
    @BindView(R.id.time)
    @JvmField
    var time: TextView? = null

    @BindView(R.id.content)
    lateinit var content: TextView

    @BindView(R.id.markerline)
    @JvmField
    var markerline: View? = null

    private var message: FormattedMessage? = null

    private val localClickListener = View.OnClickListener {
      message?.let {
        clickListener?.invoke(it)
      }
    }

    private val localLongClickListener = View.OnLongClickListener {
      message?.let {
        selectionListener?.invoke(it)
      }
      true
    }

    init {
      ButterKnife.bind(this, itemView)

      content.movementMethod = BetterLinkMovementMethod.getInstance()

      itemView.setOnClickListener(localClickListener)
      content.setOnClickListener(localClickListener)

      itemView.setOnLongClickListener(localLongClickListener)
      content.setOnLongClickListener(localLongClickListener)
    }

    fun bind(message: FormattedMessage) {
      this.message = message

      time?.text = message.time
      content.text = message.content
      markerline?.visibleIf(message.isMarkerLine)

      this.itemView.isSelected = message.isSelected
    }
  }
}

