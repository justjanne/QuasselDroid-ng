package de.kuschku.quasseldroid.ui.chat.messages

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.util.TypedValue
import de.kuschku.libquassel.protocol.Message.MessageType.*
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.protocol.MsgId
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AppearanceSettings.ColorizeNicknamesMode
import de.kuschku.quasseldroid.settings.AppearanceSettings.ShowPrefixMode
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.quassel.IrcUserUtils
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import org.intellij.lang.annotations.Language
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class QuasselMessageRenderer(
  private val context: Context,
  private val appearanceSettings: AppearanceSettings
) : MessageRenderer {
  private val timeFormatter = DateTimeFormatter.ofPattern(
    timePattern(appearanceSettings.showSeconds, appearanceSettings.use24hClock)
  )

  private fun timePattern(showSeconds: Boolean,
                          use24hClock: Boolean) = when (use24hClock to showSeconds) {
    false to true  -> "hh:mm:ss a"
    false to false -> "hh:mm a"

    true to true   -> "HH:mm:ss"
    else           -> "HH:mm"
  }

  private lateinit var senderColors: IntArray

  private val zoneId = ZoneId.systemDefault()

  private val ircFormatDeserializer = IrcFormatDeserializer(context)

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
      viewHolder.itemView.context.theme.styledAttributes(
        R.attr.colorForegroundHighlight, R.attr.colorBackgroundHighlight
      ) {
        viewHolder.time.setTextColor(getColor(0, 0))
        viewHolder.content.setTextColor(getColor(0, 0))
        viewHolder.itemView.setBackgroundColor(getColor(1, 0))
      }
    }
    if (appearanceSettings.useMonospace) {
      viewHolder.content.typeface = Typeface.MONOSPACE
    }
    val textSize = appearanceSettings.textSize.toFloat()
    viewHolder.time.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    viewHolder.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
  }

  override fun bind(holder: QuasselMessageViewHolder, message: FormattedMessage) {
    holder.time.text = message.time
    holder.content.text = message.content
    holder.markerline.visibleIf(message.markerline)
  }

  override fun render(message: QuasselDatabase.DatabaseMessage,
                      markerLine: MsgId): FormattedMessage {
    val self = Message_Flag.of(message.flag).hasFlag(Message_Flag.Self)
    val highlight = Message_Flag.of(message.flag).hasFlag(Message_Flag.Highlight)
    return when (Message_Type.of(message.type).enabledValues().firstOrNull()) {
      Message_Type.Plain        -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_plain),
          formatPrefix(message.senderPrefixes, highlight),
          formatNick(message.sender, self, highlight, false),
          formatContent(message.content, highlight)
        ),
        message.messageId == markerLine
      )
      Message_Type.Action       -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_action),
          formatPrefix(message.senderPrefixes, highlight),
          formatNick(message.sender, self, highlight, false),
          formatContent(message.content, highlight)
        ),
        message.messageId == markerLine
      )
      Message_Type.Notice       -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_notice),
          formatPrefix(message.senderPrefixes, highlight),
          formatNick(message.sender, self, highlight, false),
          formatContent(message.content, highlight)
        ),
        message.messageId == markerLine
      )
      Message_Type.Nick         -> {
        val nickSelf = message.sender == message.content || self
        FormattedMessage(
          message.messageId,
          timeFormatter.format(message.time.atZone(zoneId)),
          if (nickSelf) {
            SpanFormatter.format(
              context.getString(R.string.message_format_nick_self),
              formatPrefix(message.senderPrefixes, highlight),
              formatNick(message.sender, nickSelf, highlight, false)
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_nick),
              formatPrefix(message.senderPrefixes, highlight),
              formatNick(message.sender, nickSelf, highlight, false),
              formatPrefix(message.senderPrefixes, highlight),
              formatNick(message.content, nickSelf, highlight, false)
            )
          },
          message.messageId == markerLine
        )
      }
      Message_Type.Mode         -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_mode),
          message.content,
          formatPrefix(message.senderPrefixes, highlight),
          formatNick(message.sender, self, highlight, false)
        ),
        message.messageId == markerLine
      )
      Message_Type.Join         -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_join),
          formatPrefix(message.senderPrefixes, highlight),
          formatNick(message.sender, self, highlight, true),
          message.content
        ),
        message.messageId == markerLine
      )
      Message_Type.Part         -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        if (message.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_1),
            formatPrefix(message.senderPrefixes, highlight),
            formatNick(message.sender, self, highlight, true)
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_2),
            formatPrefix(message.senderPrefixes, highlight),
            formatNick(message.sender, self, highlight, true),
            formatContent(message.content, highlight)
          )
        },
        message.messageId == markerLine
      )
      Message_Type.Quit         -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        if (message.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_1),
            formatPrefix(message.senderPrefixes, highlight),
            formatNick(message.sender, self, highlight, true)
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_2),
            formatPrefix(message.senderPrefixes, highlight),
            formatNick(message.sender, self, highlight, true),
            formatContent(message.content, highlight)
          )
        },
        message.messageId == markerLine
      )
      Message_Type.Kick         -> {
        val (user, reason) = message.content.split(' ', limit = 2) + listOf("", "")
        FormattedMessage(
          message.messageId,
          timeFormatter.format(message.time.atZone(zoneId)),
          if (reason.isBlank()) {
            SpanFormatter.format(
              context.getString(R.string.message_format_kick_1),
              formatNick(user, false, highlight, false),
              formatPrefix(message.senderPrefixes, highlight),
              formatNick(message.sender, self, highlight, true)
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_kick_2),
              formatNick(user, false, highlight, false),
              formatPrefix(message.senderPrefixes, highlight),
              formatNick(message.sender, self, highlight, true),
              formatContent(reason, highlight)
            )
          },
          message.messageId == markerLine
        )
      }
      Message_Type.Kill         -> {
        val (user, reason) = message.content.split(' ', limit = 2) + listOf("", "")
        FormattedMessage(
          message.messageId,
          timeFormatter.format(message.time.atZone(zoneId)),
          if (reason.isBlank()) {
            SpanFormatter.format(
              context.getString(R.string.message_format_kill_1),
              formatNick(user, false, highlight, false),
              formatPrefix(message.senderPrefixes, highlight),
              formatNick(message.sender, self, highlight, true)
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_kill_2),
              formatNick(user, false, highlight, false),
              formatPrefix(message.senderPrefixes, highlight),
              formatNick(message.sender, self, highlight, true),
              formatContent(reason, highlight)
            )
          },
          message.messageId == markerLine
        )
      }
      Message_Type.NetsplitJoin -> {
        val split = message.content.split("#:#")
        val (server1, server2) = split.last().split(' ')
        val usersAffected = split.size - 1
        FormattedMessage(
          message.messageId,
          timeFormatter.format(message.time.atZone(zoneId)),
          context.resources.getQuantityString(
            R.plurals.message_netsplit_join, usersAffected, server1, server2, usersAffected
          ),
          message.messageId == markerLine
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
          ),
          message.messageId == markerLine
        )
      }
      Message_Type.Server,
      Message_Type.Info,
      Message_Type.Error        -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        formatContent(message.content, highlight),
        message.messageId == markerLine
      )
      Message_Type.Topic        -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        formatContent(message.content, highlight),
        message.messageId == markerLine
      )
      else                      -> FormattedMessage(
        message.messageId,
        timeFormatter.format(message.time.atZone(zoneId)),
        SpanFormatter.format(
          "[%d] %s%s: %s",
          message.type,
          formatPrefix(message.senderPrefixes, highlight),
          formatNick(message.sender, self, highlight, true),
          message.content
        ),
        message.messageId == markerLine
      )
    }
  }

  @Language("RegExp")
  private val scheme = "(?:(?:mailto:|magnet:|(?:[+.-]?\\w)+://)|www(?=\\.\\S+\\.))"
  @Language("RegExp")
  private val authority = "(?:(?:[,.;@:]?[-\\w]+)+\\.?|\\[[0-9a-f:.]+])?(?::\\d+)?"
  @Language("RegExp")
  private val urlChars = "(?:[,.;:]*[\\w~@/?&=+$()!%#*-])"
  @Language("RegExp")
  private val urlEnd = "((?:>|[,.;:\"]*\\s|\\b|$))"

  private val urlPattern = Regex(
    "\\b($scheme$authority(?:$urlChars*)?)$urlEnd",
    RegexOption.IGNORE_CASE
  )

  private val channelPattern = Regex(
    "((?:#|![A-Z0-9]{5})[^,:\\s]+(?::[^,:\\s]+)?)\\b",
    RegexOption.IGNORE_CASE
  )

  private fun formatContent(content: String, highlight: Boolean): CharSequence {
    val formattedText = ircFormatDeserializer.formatString(content, appearanceSettings.colorizeMirc)
    val text = SpannableString(formattedText)

    for (result in urlPattern.findAll(formattedText)) {
      val group = result.groups[1]
      if (group != null) {
        text.setSpan(
          QuasselURLSpan(group.value, highlight), group.range.start,
          group.range.start + group.value.length,
          Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
      }
    }
    /*
    for (result in channelPattern.findAll(content)) {
      text.setSpan(URLSpan(result.value), result.range.start, result.range.endInclusive, Spanned.SPAN_INCLUSIVE_INCLUSIVE)}
    */

    return text
  }

  class QuasselURLSpan(text: String, private val highlight: Boolean) : URLSpan(text) {
    override fun updateDrawState(ds: TextPaint?) {
      if (ds != null) {
        if (!highlight)
          ds.color = ds.linkColor
        ds.isUnderlineText = true
      }
    }
  }

  private fun formatNickNickImpl(nick: String, colorize: Boolean): CharSequence {
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

  private fun formatNickImpl(sender: String, colorize: Boolean, hostmask: Boolean): CharSequence {
    val nick = IrcUserUtils.nick(sender)
    val mask = IrcUserUtils.mask(sender)
    val formattedNick = formatNickNickImpl(nick, colorize)

    return if (hostmask) {
      SpanFormatter.format("%s (%s)", formattedNick, mask)
    } else {
      formattedNick
    }
  }

  private fun formatNick(sender: String, self: Boolean,
                         highlight: Boolean, showHostmask: Boolean) =
    when (appearanceSettings.colorizeNicknames) {
      ColorizeNicknamesMode.ALL          ->
        formatNickImpl(sender, !highlight, appearanceSettings.showHostmask && showHostmask)
      ColorizeNicknamesMode.ALL_BUT_MINE ->
        formatNickImpl(sender, !self && !highlight, appearanceSettings.showHostmask && showHostmask)
      ColorizeNicknamesMode.NONE         ->
        formatNickImpl(sender, false, appearanceSettings.showHostmask && showHostmask)
    }

  private fun formatPrefix(prefix: String,
                           highlight: Boolean) = when (appearanceSettings.showPrefix) {
    ShowPrefixMode.ALL     -> prefix
    ShowPrefixMode.HIGHEST -> prefix.substring(0, Math.min(prefix.length, 1))
    ShowPrefixMode.NONE    -> ""
  }
}