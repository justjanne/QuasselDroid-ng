package de.kuschku.quasseldroid_ng.ui.chat

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import de.kuschku.libquassel.protocol.Message.MessageType.*
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.ui.settings.data.RenderingSettings
import de.kuschku.quasseldroid_ng.ui.settings.data.RenderingSettings.ColorizeNicknamesMode
import de.kuschku.quasseldroid_ng.ui.settings.data.RenderingSettings.ShowPrefixMode
import de.kuschku.quasseldroid_ng.util.helper.styledAttributes
import de.kuschku.quasseldroid_ng.util.quassel.IrcUserUtils
import de.kuschku.quasseldroid_ng.util.ui.SpanFormatter
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat

class QuasselMessageRenderer(
  private val context: Context,
  private val renderingSettings: RenderingSettings
) : MessageRenderer {
  private val timeFormatter = DateTimeFormatter.ofPattern(
    if (renderingSettings.timeFormat.isNotBlank()) {
      renderingSettings.timeFormat
    } else {
      (DateFormat.getTimeFormat(context) as SimpleDateFormat).toLocalizedPattern()
    }
  )
  private lateinit var senderColors: IntArray

  private val zoneId = ZoneId.systemDefault()

  init {
    context.theme.styledAttributes(
      R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
      R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
      R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
      R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
    ) {
      senderColors = IntArray(16) {
        getColor(it, 0)
      }
    }
  }

  override fun layout(type: Message_Type?, hasHighlight: Boolean) = when (type) {
    Notice -> R.layout.widget_chatmessage_notice
    Server -> R.layout.widget_chatmessage_server
    Error  -> R.layout.widget_chatmessage_error
    Action -> R.layout.widget_chatmessage_action
    Plain  -> R.layout.widget_chatmessage_plain
    Nick, Mode, Join, Part, Quit, Kick, Kill, Info, DayChange, Topic, NetsplitJoin, NetsplitQuit,
    Invite -> R.layout.widget_chatmessage_info
    else   -> R.layout.widget_chatmessage_placeholder
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
    return when (Message_Type.of(message.type).enabledValues().firstOrNull()) {
      Message_Type.Plain  -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_plain),
          formatPrefix(message.senderPrefixes),
          formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self)),
          message.content
        )
      )
      Message_Type.Action -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_action),
          formatPrefix(message.senderPrefixes),
          formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self)),
          message.content
        )
      )
      Message_Type.Notice -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_notice),
          formatPrefix(message.senderPrefixes),
          formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self)),
          message.content
        )
      )
      Message_Type.Nick   -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_nick),
          formatPrefix(message.senderPrefixes),
          formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self)),
          formatPrefix(message.senderPrefixes),
          formatNick(message.content, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self))
        )
      )
      Message_Type.Mode   -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_mode),
          message.content,
          formatPrefix(message.senderPrefixes),
          formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self))
        )
      )
      Message_Type.Join   -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_join),
          formatPrefix(message.senderPrefixes),
          formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self))
        )
      )
      Message_Type.Part   -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        if (message.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_1),
            formatPrefix(message.senderPrefixes),
            formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self))
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_2),
            formatPrefix(message.senderPrefixes),
            formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self)),
            message.content
          )
        }
      )
      Message_Type.Quit -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        if (message.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_1),
            formatPrefix(message.senderPrefixes),
            formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self))
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_2),
            formatPrefix(message.senderPrefixes),
            formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self)),
            message.content
          )
        }
      )
      Message_Type.NetsplitJoin -> {
        val split = message.content.split("#:#")
        val (server1, server2) = split.last().split(' ')
        val usersAffected = split.size - 1
        FormattedMessage(
          message.messageId,
          timeFormatter.format(message.time.atZone(zoneId)),
          context.resources.getQuantityString(
            R.plurals.message_netsplit_join, usersAffected, server1, server2, usersAffected
          )
        )
      }
      Message_Type.NetsplitQuit -> {
        val split = message.content.split("#:#")
        val (server1, server2) = split.last().split(' ')
        val usersAffected = split.size - 1
        FormattedMessage(
          message.messageId,
          timeFormatter.format(message.time.atZone(zoneId)),
          context.resources.getQuantityString(
            R.plurals.message_netsplit_quit, usersAffected, server1, server2, usersAffected
          )
        )
      }
      Message_Type.Server,
      Message_Type.Info,
      Message_Type.Error -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        message.content
      )
      Message_Type.Topic -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        message.content
      )
      else -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          "[%d] %s%s: %s",
          message.type,
          formatPrefix(message.senderPrefixes),
          formatNick(message.sender, Message_Flag.of(message.flag).hasFlag(Message_Flag.Self)),
          message.content
        )
      )
    }
  }

  private fun formatNickImpl(sender: String, colorize: Boolean): CharSequence {
    val nick = IrcUserUtils.nick(sender)
    val spannableString = SpannableString(nick)
    if (colorize) {
      val senderColor = IrcUserUtils.senderColor(nick)
      spannableString.setSpan(
        ForegroundColorSpan(senderColors[senderColor % senderColors.size]),
        0,
        nick.length,
        SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
      )
    }
    spannableString.setSpan(
      StyleSpan(Typeface.BOLD),
      0,
      nick.length,
      SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
    )
    return spannableString
  }

  private fun formatNick(sender: String, self: Boolean)
    = when (renderingSettings.colorizeNicknames) {
    ColorizeNicknamesMode.ALL          -> formatNickImpl(sender, true)
    ColorizeNicknamesMode.ALL_BUT_MINE -> formatNickImpl(sender, !self)
    ColorizeNicknamesMode.NONE         -> formatNickImpl(sender, false)
  }

  private fun formatPrefix(prefix: String)
    = when (renderingSettings.showPrefix) {
    ShowPrefixMode.ALL   -> prefix
    ShowPrefixMode.FIRST -> prefix.substring(0, Math.min(prefix.length, 1))
    ShowPrefixMode.NONE  -> ""
  }
}