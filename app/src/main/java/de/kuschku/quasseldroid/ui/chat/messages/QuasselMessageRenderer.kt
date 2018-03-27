package de.kuschku.quasseldroid.ui.chat.messages

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
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
import de.kuschku.libquassel.util.hasFlag
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AppearanceSettings.ColorizeNicknamesMode
import de.kuschku.quasseldroid.settings.AppearanceSettings.ShowPrefixMode
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.quassel.IrcUserUtils
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage
import org.intellij.lang.annotations.Language
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import javax.inject.Inject

class QuasselMessageRenderer @Inject constructor(
  private val appearanceSettings: AppearanceSettings,
  private val ircFormatDeserializer: IrcFormatDeserializer
) : MessageRenderer {
  private val timeFormatter = DateTimeFormatter.ofPattern(
    timePattern(appearanceSettings.showSeconds, appearanceSettings.use24hClock)
  )

  private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

  val monospaceItalic = Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC)

  private fun timePattern(showSeconds: Boolean,
                          use24hClock: Boolean) = when (use24hClock to showSeconds) {
    false to true  -> "hh:mm:ss a"
    false to false -> "hh:mm a"

    true to true   -> "HH:mm:ss"
    else           -> "HH:mm"
  }

  private lateinit var senderColors: IntArray

  private val zoneId = ZoneId.systemDefault()

  override fun layout(type: Message_Type?, hasHighlight: Boolean) = when (type) {
    Notice    -> R.layout.widget_chatmessage_notice
    Server    -> R.layout.widget_chatmessage_server
    Error     -> R.layout.widget_chatmessage_error
    Action    -> R.layout.widget_chatmessage_action
    Plain     -> R.layout.widget_chatmessage_plain
    Nick, Mode, Join, Part, Quit, Kick, Kill, Info, Topic, NetsplitJoin, NetsplitQuit,
    Invite    -> R.layout.widget_chatmessage_info
    DayChange -> R.layout.widget_chatmessage_daychange
    else      -> R.layout.widget_chatmessage_placeholder
  }

  override fun init(viewHolder: MessageAdapter.QuasselMessageViewHolder,
                    messageType: Message_Type?,
                    hasHighlight: Boolean) {
    if (hasHighlight) {
      viewHolder.itemView.context.theme.styledAttributes(
        R.attr.colorForegroundHighlight, R.attr.colorBackgroundHighlight,
        R.attr.backgroundMenuItem
      ) {
        viewHolder.time?.setTextColor(getColor(0, 0))
        viewHolder.content.setTextColor(getColor(0, 0))
        viewHolder.itemView.background = LayerDrawable(
          arrayOf(
            ColorDrawable(getColor(1, 0)),
            getDrawable(2)
          )
        )
      }
    }
    if (appearanceSettings.useMonospace) {
      val old = viewHolder.content.typeface
      if (old.isItalic) {
        viewHolder.content.typeface = monospaceItalic
      } else {
        viewHolder.content.typeface = Typeface.MONOSPACE
      }
    }
    val textSize = appearanceSettings.textSize.toFloat()
    viewHolder.time?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    viewHolder.content.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
  }

  override fun bind(holder: MessageAdapter.QuasselMessageViewHolder, message: FormattedMessage,
                    original: QuasselDatabase.DatabaseMessage) =
    Message_Type.of(original.type).hasFlag(DayChange).let { isDayChange ->
      holder.bind(message, !isDayChange, !isDayChange)
    }

  override fun render(context: Context,
                      message: DisplayMessage): FormattedMessage {
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

    val self = Message_Flag.of(message.content.flag).hasFlag(Message_Flag.Self)
    val highlight = Message_Flag.of(message.content.flag).hasFlag(Message_Flag.Highlight)
    return when (Message_Type.of(message.content.type).enabledValues().firstOrNull()) {
      Message_Type.Plain        -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_plain),
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, false),
          formatContent(context, message.content.content, highlight)
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Action       -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_action),
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, false),
          formatContent(context, message.content.content, highlight)
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Notice       -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_notice),
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, false),
          formatContent(context, message.content.content, highlight)
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Nick         -> {
        val nickSelf = message.content.sender == message.content.content || self
        FormattedMessage(
          message.content.messageId,
          timeFormatter.format(message.content.time.atZone(zoneId)),
          if (nickSelf) {
            SpanFormatter.format(
              context.getString(R.string.message_format_nick_self),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender, nickSelf, highlight, false)
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_nick),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender, nickSelf, highlight, false),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.content, nickSelf, highlight, false)
            )
          },
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.Mode         -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_mode),
          message.content.content,
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, false)
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Join         -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        SpanFormatter.format(
          context.getString(R.string.message_format_join),
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, true),
          message.content.content
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Part         -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        if (message.content.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_1),
            formatPrefix(message.content.senderPrefixes, highlight),
            formatNick(message.content.sender, self, highlight, true)
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_2),
            formatPrefix(message.content.senderPrefixes, highlight),
            formatNick(message.content.sender, self, highlight, true),
            formatContent(context, message.content.content, highlight)
          )
        },
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Quit         -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        if (message.content.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_1),
            formatPrefix(message.content.senderPrefixes, highlight),
            formatNick(message.content.sender, self, highlight, true)
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_2),
            formatPrefix(message.content.senderPrefixes, highlight),
            formatNick(message.content.sender, self, highlight, true),
            formatContent(context, message.content.content, highlight)
          )
        },
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Kick         -> {
        val (user, reason) = message.content.content.split(' ', limit = 2) + listOf("", "")
        FormattedMessage(
          message.content.messageId,
          timeFormatter.format(message.content.time.atZone(zoneId)),
          if (reason.isBlank()) {
            SpanFormatter.format(
              context.getString(R.string.message_format_kick_1),
              formatNick(user, false, highlight, false),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender, self, highlight, true)
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_kick_2),
              formatNick(user, false, highlight, false),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender, self, highlight, true),
              formatContent(context, reason, highlight)
            )
          },
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.Kill         -> {
        val (user, reason) = message.content.content.split(' ', limit = 2) + listOf("", "")
        FormattedMessage(
          message.content.messageId,
          timeFormatter.format(message.content.time.atZone(zoneId)),
          if (reason.isBlank()) {
            SpanFormatter.format(
              context.getString(R.string.message_format_kill_1),
              formatNick(user, false, highlight, false),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender, self, highlight, true)
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_kill_2),
              formatNick(user, false, highlight, false),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender, self, highlight, true),
              formatContent(context, reason, highlight)
            )
          },
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.NetsplitJoin -> {
        val split = message.content.content.split("#:#")
        val (server1, server2) = split.last().split(' ')
        val usersAffected = split.size - 1
        FormattedMessage(
          message.content.messageId,
          timeFormatter.format(message.content.time.atZone(zoneId)),
          context.resources.getQuantityString(
            R.plurals.message_netsplit_join, usersAffected, server1, server2, usersAffected
          ),
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.NetsplitQuit -> {
        val split = message.content.content.split("#:#")
        val (server1, server2) = split.last().split(' ')
        val usersAffected = split.size - 1
        FormattedMessage(
          message.content.messageId,
          timeFormatter.format(message.content.time.atZone(zoneId)),
          context.resources.getQuantityString(
            R.plurals.message_netsplit_quit, usersAffected, server1, server2, usersAffected
          ),
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.Server,
      Message_Type.Info,
      Message_Type.Error        -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        formatContent(context, message.content.content, highlight),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Topic        -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        formatContent(context, message.content.content, highlight),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      DayChange                 -> FormattedMessage(
        message.content.messageId,
        "",
        dateFormatter.format(message.content.time.atZone(zoneId)),
        isMarkerLine = false,
        isExpanded = false,
        isSelected = false
      )
      else                      -> FormattedMessage(
        message.content.messageId,
        timeFormatter.format(message.content.time.atZone(zoneId)),
        SpanFormatter.format(
          "[%d] %s%s: %s",
          message.content.type,
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, true),
          message.content.content
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
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

  private fun formatContent(context: Context, content: String, highlight: Boolean): CharSequence {
    val formattedText = ircFormatDeserializer.formatString(
      context, content, appearanceSettings.colorizeMirc
    )
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