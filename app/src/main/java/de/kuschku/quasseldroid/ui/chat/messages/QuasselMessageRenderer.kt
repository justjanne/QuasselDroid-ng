package de.kuschku.quasseldroid.ui.chat.messages

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import de.kuschku.libquassel.protocol.Message.MessageType.*
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.util.IrcUserUtils
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.settings.MessageSettings.ColorizeNicknamesMode
import de.kuschku.quasseldroid.settings.MessageSettings.ShowPrefixMode
import de.kuschku.quasseldroid.util.AvatarHelper
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import de.kuschku.quasseldroid.util.ui.TextDrawable
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import javax.inject.Inject
import kotlin.math.roundToInt

class QuasselMessageRenderer @Inject constructor(
  private val messageSettings: MessageSettings,
  private val contentFormatter: ContentFormatter
) : MessageRenderer {
  private val timeFormatter = DateTimeFormatter.ofPattern(
    timePattern(messageSettings.showSeconds, messageSettings.use24hClock)
  )

  private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

  private val monospaceItalic = Typeface.create(Typeface.MONOSPACE, Typeface.ITALIC)

  private fun timePattern(showSeconds: Boolean,
                          use24hClock: Boolean) = when (use24hClock to showSeconds) {
    false to true  -> "hh:mm:ss a"
    false to false -> "hh:mm a"

    true to true   -> "HH:mm:ss"
    else           -> "HH:mm"
  }

  private lateinit var mircColors: IntArray
  private lateinit var senderColors: IntArray
  private var selfColor: Int = 0

  private val zoneId = ZoneId.systemDefault()

  override fun layout(type: Message_Type?, hasHighlight: Boolean,
                      isFollowUp: Boolean, isEmoji: Boolean) = when (type) {
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
                    hasHighlight: Boolean,
                    isFollowUp: Boolean,
                    isEmoji: Boolean) {
    if (hasHighlight) {
      viewHolder.itemView.context.theme.styledAttributes(
        R.attr.colorForegroundHighlight, R.attr.colorBackgroundHighlight,
        R.attr.backgroundMenuItem
      ) {
        viewHolder.timeLeft?.setTextColor(getColor(0, 0))
        viewHolder.timeRight?.setTextColor(getColor(0, 0))
        viewHolder.name?.setTextColor(getColor(0, 0))
        viewHolder.combined?.setTextColor(getColor(0, 0))
        viewHolder.content?.setTextColor(getColor(0, 0))
        viewHolder.itemView.background = LayerDrawable(
          arrayOf(
            ColorDrawable(getColor(1, 0)),
            getDrawable(2)
          )
        )
      }
    }

    val avatarContainer = viewHolder.itemView.findViewById<View>(R.id.avatar_container)
    val avatarPlaceholder = viewHolder.itemView.findViewById<View>(R.id.avatar_placeholder)

    if (messageSettings.useMonospace) {
      viewHolder.content?.typeface = if (viewHolder.content?.typeface?.isItalic == true) monospaceItalic else Typeface.MONOSPACE
      viewHolder.combined?.typeface = if (viewHolder.combined?.typeface?.isItalic == true) monospaceItalic else Typeface.MONOSPACE
    }

    viewHolder.avatar?.visibleIf(!isFollowUp)
    avatarContainer?.visibleIf(messageSettings.showAvatars)
    avatarPlaceholder?.visibleIf(messageSettings.showAvatars)
    val separateLine = viewHolder.content != null && viewHolder.name != null && messageSettings.nicksOnNewLine
    viewHolder.name?.visibleIf(separateLine && !isFollowUp)
    viewHolder.content?.visibleIf(separateLine)
    viewHolder.combined?.visibleIf(!separateLine)

    viewHolder.timeLeft?.visibleIf(!messageSettings.timeAtEnd)
    viewHolder.timeRight?.visibleIf(messageSettings.timeAtEnd)

    val textSize = messageSettings.textSize.toFloat()
    viewHolder.timeLeft?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    viewHolder.timeRight?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize * 0.9f)
    viewHolder.name?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    val contentSize = if (messageSettings.largerEmoji && isEmoji) textSize * 2f else textSize
    viewHolder.content?.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize)
    viewHolder.combined?.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize)
    val avatarSize = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      textSize * 2.5f,
      viewHolder.itemView.context.resources.displayMetrics
    ).roundToInt()
    viewHolder.avatar?.layoutParams =
      FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, avatarSize)
    avatarContainer?.layoutParams =
      LinearLayout.LayoutParams(avatarSize, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
        val margin = viewHolder.itemView.context.resources.getDimensionPixelSize(R.dimen.message_horizontal)
        setMargins(0, 0, margin, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          marginEnd = margin
        }
      }
    avatarPlaceholder?.layoutParams =
      LinearLayout.LayoutParams(avatarSize, LinearLayout.LayoutParams.MATCH_PARENT).apply {
        val margin = viewHolder.itemView.context.resources.getDimensionPixelSize(R.dimen.message_horizontal)
        setMargins(0, 0, margin, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          marginEnd = margin
        }
      }
  }

  override fun bind(holder: MessageAdapter.QuasselMessageViewHolder, message: FormattedMessage,
                    original: QuasselDatabase.DatabaseMessage) =
    Message_Type.of(original.type).hasFlag(DayChange).let { isDayChange ->
      holder.bind(message, original, !isDayChange, !isDayChange)
    }

  override fun updateColors(context: Context) {
    mircColors = context.theme.styledAttributes(
      R.attr.mircColor00, R.attr.mircColor01, R.attr.mircColor02, R.attr.mircColor03,
      R.attr.mircColor04, R.attr.mircColor05, R.attr.mircColor06, R.attr.mircColor07,
      R.attr.mircColor08, R.attr.mircColor09, R.attr.mircColor10, R.attr.mircColor11,
      R.attr.mircColor12, R.attr.mircColor13, R.attr.mircColor14, R.attr.mircColor15,
      R.attr.mircColor16, R.attr.mircColor17, R.attr.mircColor18, R.attr.mircColor19,
      R.attr.mircColor20, R.attr.mircColor21, R.attr.mircColor22, R.attr.mircColor23,
      R.attr.mircColor24, R.attr.mircColor25, R.attr.mircColor26, R.attr.mircColor27,
      R.attr.mircColor28, R.attr.mircColor29, R.attr.mircColor30, R.attr.mircColor31,
      R.attr.mircColor32, R.attr.mircColor33, R.attr.mircColor34, R.attr.mircColor35,
      R.attr.mircColor36, R.attr.mircColor37, R.attr.mircColor38, R.attr.mircColor39,
      R.attr.mircColor40, R.attr.mircColor41, R.attr.mircColor42, R.attr.mircColor43,
      R.attr.mircColor44, R.attr.mircColor45, R.attr.mircColor46, R.attr.mircColor47,
      R.attr.mircColor48, R.attr.mircColor49, R.attr.mircColor50, R.attr.mircColor51,
      R.attr.mircColor52, R.attr.mircColor53, R.attr.mircColor54, R.attr.mircColor55,
      R.attr.mircColor56, R.attr.mircColor57, R.attr.mircColor58, R.attr.mircColor59,
      R.attr.mircColor60, R.attr.mircColor61, R.attr.mircColor62, R.attr.mircColor63,
      R.attr.mircColor64, R.attr.mircColor65, R.attr.mircColor66, R.attr.mircColor67,
      R.attr.mircColor68, R.attr.mircColor69, R.attr.mircColor70, R.attr.mircColor71,
      R.attr.mircColor72, R.attr.mircColor73, R.attr.mircColor74, R.attr.mircColor75,
      R.attr.mircColor76, R.attr.mircColor77, R.attr.mircColor78, R.attr.mircColor79,
      R.attr.mircColor80, R.attr.mircColor81, R.attr.mircColor82, R.attr.mircColor83,
      R.attr.mircColor84, R.attr.mircColor85, R.attr.mircColor86, R.attr.mircColor87,
      R.attr.mircColor88, R.attr.mircColor89, R.attr.mircColor90, R.attr.mircColor91,
      R.attr.mircColor92, R.attr.mircColor93, R.attr.mircColor94, R.attr.mircColor95,
      R.attr.mircColor96, R.attr.mircColor97, R.attr.mircColor98
    ) {
      IntArray(99) {
        getColor(it, 0)
      }
    }

    context.theme.styledAttributes(
      R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
      R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
      R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
      R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF,
      R.attr.colorForegroundSecondary
    ) {
      senderColors = IntArray(16) {
        getColor(it, 0)
      }
      selfColor = getColor(16, 0)
    }
  }

  override fun render(context: Context, message: DisplayMessage): FormattedMessage {
    val avatarSize = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      messageSettings.textSize * 2.5f,
      context.resources.displayMetrics
    ).roundToInt()

    val self = Message_Flag.of(message.content.flag).hasFlag(Message_Flag.Self)
    val highlight = Message_Flag.of(message.content.flag).hasFlag(Message_Flag.Highlight)
    return when (Message_Type.of(message.content.type).enabledValues().firstOrNull()) {
      Message_Type.Plain        -> {
        val realName = contentFormatter.format(mircColors, message.content.realName, highlight)
        val nick = SpannableStringBuilder().apply {
          append(formatPrefix(message.content.senderPrefixes, highlight))
          append(formatNick(
            message.content.sender,
            self,
            highlight,
            messageSettings.showHostmaskPlain && messageSettings.nicksOnNewLine
          ))
          if (messageSettings.showRealNames) {
            append(" ")
            append(realName)
          }
        }
        val content = contentFormatter.format(mircColors, message.content.content, highlight)
        val nickName = HostmaskHelper.nick(message.content.sender)
        val senderColorIndex = IrcUserUtils.senderColor(nickName)
        val rawInitial = nickName.trimStart('-', '_', '[', ']', '{', '}', '|', '`', '^', '.', '\\')
                           .firstOrNull() ?: nickName.firstOrNull()
        val initial = rawInitial?.toUpperCase().toString()
        val senderColor = if (Message_Flag.of(message.content.flag).hasFlag(Message_Flag.Self))
          selfColor
        else
          senderColors[senderColorIndex]

        FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          name = nick,
          content = content,
          combined = SpannableStringBuilder().apply {
            append(nick)
            append(": ")
            append(content)
          },
          realName = realName,
          avatarUrls = AvatarHelper.avatar(messageSettings, message.content, avatarSize),
          fallbackDrawable = TextDrawable.builder().buildRound(initial, senderColor),
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.Action       -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        combined = SpanFormatter.format(
          context.getString(R.string.message_format_action),
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, false),
          contentFormatter.format(mircColors, message.content.content, highlight)
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Notice       -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        combined = SpanFormatter.format(
          context.getString(R.string.message_format_notice),
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, false),
          contentFormatter.format(mircColors, message.content.content, highlight)
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Nick         -> {
        val nickSelf = message.content.sender == message.content.content || self
        FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          combined = if (nickSelf) {
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
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        combined = SpanFormatter.format(
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
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        combined = SpanFormatter.format(
          context.getString(R.string.message_format_join),
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, messageSettings.showHostmaskActions),
          message.content.content
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Part         -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        combined = if (message.content.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_1),
            formatPrefix(message.content.senderPrefixes, highlight),
            formatNick(message.content.sender, self, highlight, messageSettings.showHostmaskActions)
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_2),
            formatPrefix(message.content.senderPrefixes, highlight),
            formatNick(message.content.sender,
                       self,
                       highlight,
                       messageSettings.showHostmaskActions),
            contentFormatter.format(mircColors, message.content.content, highlight)
          )
        },
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Quit         -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        combined = if (message.content.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_1),
            formatPrefix(message.content.senderPrefixes, highlight),
            formatNick(message.content.sender, self, highlight, messageSettings.showHostmaskActions)
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_2),
            formatPrefix(message.content.senderPrefixes, highlight),
            formatNick(message.content.sender,
                       self,
                       highlight,
                       messageSettings.showHostmaskActions),
            contentFormatter.format(mircColors, message.content.content, highlight)
          )
        },
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Kick         -> {
        val (user, reason) = message.content.content.split(' ', limit = 2) + listOf("", "")
        FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          combined = if (reason.isBlank()) {
            SpanFormatter.format(
              context.getString(R.string.message_format_kick_1),
              formatNick(user, false, highlight, false),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender,
                         self,
                         highlight,
                         messageSettings.showHostmaskActions)
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_kick_2),
              formatNick(user, false, highlight, false),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender,
                         self,
                         highlight,
                         messageSettings.showHostmaskActions),
              contentFormatter.format(mircColors, reason, highlight)
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
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          combined = if (reason.isBlank()) {
            SpanFormatter.format(
              context.getString(R.string.message_format_kill_1),
              formatNick(user, false, highlight, false),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender,
                         self,
                         highlight,
                         messageSettings.showHostmaskActions)
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_kill_2),
              formatNick(user, false, highlight, false),
              formatPrefix(message.content.senderPrefixes, highlight),
              formatNick(message.content.sender,
                         self,
                         highlight,
                         messageSettings.showHostmaskActions),
              contentFormatter.format(mircColors, reason, highlight)
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
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          combined = context.resources.getQuantityString(
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
        val it = FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          combined = context.resources.getQuantityString(
            R.plurals.message_netsplit_quit, usersAffected, server1, server2, usersAffected
          ),
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
        println("hi")
        it
      }
      Message_Type.Server,
      Message_Type.Info,
      Message_Type.Error        -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        combined = contentFormatter.format(mircColors, message.content.content, highlight),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Topic        -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        combined = contentFormatter.format(mircColors, message.content.content, highlight),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.DayChange    -> FormattedMessage(
        id = message.content.messageId,
        time = "",
        combined = dateFormatter.format(message.content.time.atZone(zoneId)),
        isMarkerLine = false,
        isExpanded = false,
        isSelected = false
      )
      else                      -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        combined = SpanFormatter.format(
          "[%d] %s%s: %s",
          message.content.type,
          formatPrefix(message.content.senderPrefixes, highlight),
          formatNick(message.content.sender, self, highlight, messageSettings.showHostmaskActions),
          message.content.content
        ),
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
    }
  }

  private fun formatNickNickImpl(nick: String, colorize: Boolean): CharSequence {
    val spannableString = SpannableString(nick)
    if (colorize) {
      val senderColor = IrcUserUtils.senderColor(nick)
      spannableString.setSpan(
        ForegroundColorSpan(senderColors[(senderColor + senderColors.size) % senderColors.size]),
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

  private fun formatNick(sender: String, self: Boolean, highlight: Boolean, showHostmask: Boolean) =
    when (messageSettings.colorizeNicknames) {
      ColorizeNicknamesMode.ALL          ->
        formatNickImpl(sender, !highlight, showHostmask)
      ColorizeNicknamesMode.ALL_BUT_MINE ->
        formatNickImpl(sender, !self && !highlight, showHostmask)
      ColorizeNicknamesMode.NONE         ->
        formatNickImpl(sender, false, showHostmask)
    }

  private fun formatPrefix(prefix: String, highlight: Boolean) = when (messageSettings.showPrefix) {
    ShowPrefixMode.ALL     -> prefix
    ShowPrefixMode.HIGHEST -> prefix.substring(0, Math.min(prefix.length, 1))
    ShowPrefixMode.NONE    -> ""
  }
}
