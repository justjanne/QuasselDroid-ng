package de.kuschku.quasseldroid_ng.ui.chat

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import de.kuschku.libquassel.protocol.Message.MessageType.*
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.util.quassel.IrcUserUtils
import de.kuschku.quasseldroid_ng.util.ui.SpanFormatter
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat

class QuasselMessageRenderer(context: Context) : MessageRenderer {
  private val timeFormatter = DateTimeFormatter.ofPattern(
    (DateFormat.getTimeFormat(context) as SimpleDateFormat).toLocalizedPattern()
  )
  private val senderColors: IntArray

  private val zoneId = ZoneId.systemDefault()

  init {
    val typedArray = context.obtainStyledAttributes(
      intArrayOf(
        R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
        R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
        R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
        R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
      )
    )
    senderColors = IntArray(16) {
      typedArray.getColor(it, 0)
    }
    typedArray.recycle()
  }

  override fun layout(type: Message_Type?, hasHighlight: Boolean)
    = when (type) {
    Nick, Notice, Mode, Join, Part, Quit, Kick, Kill, Server, Info, DayChange, Topic, NetsplitJoin,
    NetsplitQuit, Invite -> R.layout.widget_chatmessage_server
    Error                -> R.layout.widget_chatmessage_error
    Action               -> R.layout.widget_chatmessage_action
    Plain                -> R.layout.widget_chatmessage_plain
    else                 -> R.layout.widget_chatmessage_plain
  }

  override fun init(viewHolder: QuasselMessageViewHolder,
                    messageType: Message_Type?,
                    hasHighlight: Boolean) {
    if (hasHighlight) {
      val attrs = intArrayOf(R.attr.colorBackgroundHighlight)
      val colors = viewHolder.itemView.context.obtainStyledAttributes(attrs)
      viewHolder.itemView.setBackgroundColor(colors.getColor(0, 0))
      colors.recycle()
    }
  }

  override fun bind(holder: QuasselMessageViewHolder, message: FormattedMessage) {
    holder.time.text = message.time
    holder.content.text = message.content
  }

  override fun render(message: QuasselDatabase.DatabaseMessage): FormattedMessage {
    return FormattedMessage(
      message.messageId,
      timeFormatter.format(message.time.atZone(zoneId)),
      SpanFormatter.format(
        "%s%s: %s",
        message.senderPrefixes,
        formatNick(message.sender),
        message.content
      )
    )
  }

  private fun formatNick(sender: String): CharSequence {
    val nick = IrcUserUtils.nick(sender)
    val senderColor = IrcUserUtils.senderColor(nick)
    val spannableString = SpannableString(nick)
    spannableString.setSpan(
      ForegroundColorSpan(senderColors[senderColor % senderColors.size]),
      0,
      nick.length,
      SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
    )
    spannableString.setSpan(
      StyleSpan(Typeface.BOLD),
      0,
      nick.length,
      SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
    )
    return spannableString
  }
}