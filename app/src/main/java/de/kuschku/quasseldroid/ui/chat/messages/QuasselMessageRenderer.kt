/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
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

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import de.kuschku.libquassel.protocol.Message.MessageType.*
import de.kuschku.libquassel.protocol.Message_Flag
import de.kuschku.libquassel.protocol.Message_Type
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.libquassel.util.irc.SenderColorUtil
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.ColorContext
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.ui.SpanFormatter
import de.kuschku.quasseldroid.viewmodel.EditorViewModel
import de.kuschku.quasseldroid.viewmodel.data.FormattedMessage
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt

class QuasselMessageRenderer @Inject constructor(
  context: Context,
  private val messageSettings: MessageSettings,
  private val contentFormatter: ContentFormatter,
  private val ircFormatDeserializer: IrcFormatDeserializer
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

  private val senderColors: IntArray = context.theme.styledAttributes(
    R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
    R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
    R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
    R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
  ) {
    IntArray(16) {
      getColor(it, 0)
    }
  }

  private val monochromeHighlights = context.theme.styledAttributes(
    R.attr.colorForegroundHighlightMonochrome
  ) {
    getBoolean(0, false)
  }

  private val selfColor: Int = context.theme.styledAttributes(R.attr.colorForegroundSecondary) {
    getColor(0, 0)
  }

  private val colorContext = ColorContext(context, messageSettings)

  private val zoneId = ZoneId.systemDefault()

  override fun layout(type: Message_Type?,
                      hasHighlight: Boolean,
                      isFollowUp: Boolean,
                      isEmoji: Boolean,
                      isSelf: Boolean) = when (type) {
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
                    isEmoji: Boolean,
                    isSelf: Boolean) {
    if (hasHighlight) {
      viewHolder.itemView.context.theme.styledAttributes(
        R.attr.colorForegroundHighlight, R.attr.colorForegroundHighlightSecondary,
        R.attr.colorBackgroundHighlight, R.attr.backgroundMenuItem
      ) {
        viewHolder.timeLeft?.setTextColor(getColor(1, 0))
        viewHolder.timeRight?.setTextColor(getColor(1, 0))
        viewHolder.name?.setTextColor(getColor(1, 0))
        viewHolder.realname?.setTextColor(getColor(1, 0))
        viewHolder.combined?.setTextColor(getColor(0, 0))
        viewHolder.content?.setTextColor(getColor(0, 0))
        viewHolder.messageContainer?.background = LayerDrawable(
          arrayOf(
            ColorDrawable(getColor(2, 0)),
            getDrawable(3)
          )
        )
      }
    }


    if (isSelf && messageSettings.highlightOwnMessages) {
      viewHolder.itemView.context.theme.styledAttributes(
        R.attr.colorBackgroundSecondary,
        R.attr.backgroundMenuItem
      ) {
        viewHolder.messageContainer?.background = LayerDrawable(
          arrayOf(
            ColorDrawable(getColor(0, 0)),
            getDrawable(1)
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
    avatarContainer?.visibleIf(messageSettings.showAvatars && messageSettings.nicksOnNewLine)
    avatarPlaceholder?.visibleIf(messageSettings.showAvatars && messageSettings.nicksOnNewLine)
    val separateLine = viewHolder.content != null && viewHolder.name != null && messageSettings.nicksOnNewLine
    viewHolder.name?.visibleIf(separateLine && !isFollowUp)
    viewHolder.realname?.visibleIf(separateLine && !isFollowUp && messageSettings.showRealNames && messageSettings.nicksOnNewLine)
    viewHolder.content?.visibleIf(separateLine)
    viewHolder.combined?.visibleIf(!separateLine)

    viewHolder.timeLeft?.visibleIf(!messageSettings.timeAtEnd)
    viewHolder.timeRight?.visibleIf(messageSettings.timeAtEnd)

    val textSize = messageSettings.textSize.toFloat()
    viewHolder.timeLeft?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    viewHolder.timeRight?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize * 0.9f)
    viewHolder.name?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    viewHolder.realname?.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    val contentSize = if (messageSettings.largerEmoji && isEmoji) textSize * 2f else textSize
    viewHolder.content?.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize)
    viewHolder.combined?.setTextSize(TypedValue.COMPLEX_UNIT_SP, contentSize)
    val avatarContainerSize = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      textSize * 2.5f,
      viewHolder.itemView.context.resources.displayMetrics
    ).roundToInt()
    val avatarSize = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      if (messageType == Plain) textSize * 2.5f
      else textSize * 1.5f,
      viewHolder.itemView.context.resources.displayMetrics
    ).roundToInt()
    viewHolder.avatar?.layoutParams = FrameLayout.LayoutParams(avatarSize, avatarSize).apply {
      gravity = Gravity.END
    }
    avatarContainer?.layoutParams =
      LinearLayout.LayoutParams(avatarContainerSize, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
        val margin = viewHolder.itemView.context.resources.getDimensionPixelSize(R.dimen.message_horizontal)
        setMargins(0, 0, margin, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          marginEnd = margin
        }
      }
    avatarPlaceholder?.layoutParams =
      LinearLayout.LayoutParams(avatarContainerSize, LinearLayout.LayoutParams.MATCH_PARENT).apply {
        val margin = viewHolder.itemView.context.resources.getDimensionPixelSize(R.dimen.message_horizontal)
        setMargins(0, 0, margin, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          marginEnd = margin
        }
      }

    viewHolder.messageContainer?.apply {
      val horizontal = context.resources.getDimensionPixelSize(R.dimen.message_horizontal)
      val vertical = context.resources.getDimensionPixelSize(R.dimen.message_vertical)

      setPadding(horizontal, vertical, horizontal, vertical)
    }
  }

  override fun bind(holder: MessageAdapter.QuasselMessageViewHolder, message: FormattedMessage,
                    original: QuasselDatabase.MessageData) =
    holder.bind(message,
                original,
                hasDayChange = message.hasDayChange,
                messageSettings = messageSettings)

  override fun render(context: Context, message: DisplayMessage): FormattedMessage {
    val avatarSize = TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_SP,
      messageSettings.textSize * 2.5f,
      context.resources.displayMetrics
    ).roundToInt()

    val self = message.content.flag.hasFlag(Message_Flag.Self)
    val highlight = message.content.flag.hasFlag(Message_Flag.Highlight)
    val monochromeForeground = highlight && monochromeHighlights
    return when (message.content.type.enabledValues().firstOrNull()) {
      Message_Type.Plain        -> {
        val realName = ircFormatDeserializer.formatString(message.content.realName,
                                                          !monochromeForeground)
        val nick = SpannableStringBuilder().apply {
          append(contentFormatter.formatPrefix(message.content.senderPrefixes))
          append(contentFormatter.formatNick(
            message.content.sender,
            self,
            monochromeForeground,
            false
          ))
        }
        val content = contentFormatter.formatContent(message.content.content,
                                                     monochromeForeground,
                                                     message.content.networkId)
        val nickName = HostmaskHelper.nick(message.content.sender)
        val senderColorIndex = SenderColorUtil.senderColor(nickName)
        val rawInitial = nickName.trimStart(*EditorViewModel.IGNORED_CHARS)
                           .firstOrNull() ?: nickName.firstOrNull()
        val initial = rawInitial?.toUpperCase().toString()
        val useSelfColor = when (messageSettings.colorizeNicknames) {
          MessageSettings.ColorizeNicknamesMode.ALL          -> false
          MessageSettings.ColorizeNicknamesMode.ALL_BUT_MINE ->
            message.content.flag.hasFlag(Message_Flag.Self)
          MessageSettings.ColorizeNicknamesMode.NONE         -> true
        }
        val senderColor = if (useSelfColor) selfColor else senderColors[senderColorIndex]

        FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          dayChange = formatDayChange(message),
          name = nick,
          content = content,
          combined = SpannableStringBuilder().apply {
            append(nick)
            append(": ")
            append(content)
          },
          realName = realName,
          avatarUrls = AvatarHelper.avatar(messageSettings, message.content, avatarSize),
          fallbackDrawable = colorContext.buildTextDrawable(initial, senderColor),
          hasDayChange = message.hasDayChange,
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.Action       -> {
        val nickName = HostmaskHelper.nick(message.content.sender)
        val senderColorIndex = SenderColorUtil.senderColor(nickName)
        val rawInitial = nickName.trimStart(*EditorViewModel.IGNORED_CHARS)
                           .firstOrNull() ?: nickName.firstOrNull()
        val initial = rawInitial?.toUpperCase().toString()
        val useSelfColor = when (messageSettings.colorizeNicknames) {
          MessageSettings.ColorizeNicknamesMode.ALL          -> false
          MessageSettings.ColorizeNicknamesMode.ALL_BUT_MINE ->
            message.content.flag.hasFlag(Message_Flag.Self)
          MessageSettings.ColorizeNicknamesMode.NONE         -> true
        }
        val senderColor = if (useSelfColor) selfColor else senderColors[senderColorIndex]

        FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          dayChange = formatDayChange(message),
          combined = SpanFormatter.format(
            context.getString(R.string.message_format_action),
            contentFormatter.formatPrefix(message.content.senderPrefixes),
            contentFormatter.formatNick(message.content.sender, self, monochromeForeground, false),
            contentFormatter.formatContent(message.content.content,
                                           monochromeForeground,
                                           message.content.networkId)
          ),
          avatarUrls = AvatarHelper.avatar(messageSettings, message.content, avatarSize),
          fallbackDrawable = colorContext.buildTextDrawable(initial, senderColor),
          hasDayChange = message.hasDayChange,
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.Notice       -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        dayChange = formatDayChange(message),
        combined = SpanFormatter.format(
          context.getString(R.string.message_format_notice),
          contentFormatter.formatPrefix(message.content.senderPrefixes),
          contentFormatter.formatNick(message.content.sender, self, monochromeForeground, false),
          contentFormatter.formatContent(message.content.content,
                                         monochromeForeground,
                                         message.content.networkId)
        ),
        hasDayChange = message.hasDayChange,
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Nick         -> {
        val nickSelf = message.content.sender == message.content.content || self
        FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          dayChange = formatDayChange(message),
          combined = if (nickSelf) {
            SpanFormatter.format(
              context.getString(R.string.message_format_nick_self),
              contentFormatter.formatPrefix(message.content.senderPrefixes),
              contentFormatter.formatNick(
                message.content.sender,
                nickSelf,
                monochromeForeground,
                false
              )
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_nick),
              contentFormatter.formatPrefix(message.content.senderPrefixes),
              contentFormatter.formatNick(
                message.content.sender,
                nickSelf,
                monochromeForeground,
                false
              ),
              contentFormatter.formatPrefix(message.content.senderPrefixes),
              contentFormatter.formatNick(
                message.content.content,
                nickSelf,
                monochromeForeground,
                false
              )
            )
          },
          hasDayChange = message.hasDayChange,
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.Mode         -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        dayChange = formatDayChange(message),
        combined = SpanFormatter.format(
          context.getString(R.string.message_format_mode),
          message.content.content,
          contentFormatter.formatPrefix(message.content.senderPrefixes),
          contentFormatter.formatNick(message.content.sender, self, monochromeForeground, false)
        ),
        hasDayChange = message.hasDayChange,
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Join         -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        dayChange = formatDayChange(message),
        combined = SpanFormatter.format(
          context.getString(R.string.message_format_join),
          contentFormatter.formatPrefix(message.content.senderPrefixes),
          contentFormatter.formatNick(
            message.content.sender,
            self,
            monochromeForeground,
            messageSettings.showHostmaskActions
          ),
          message.content.content
        ),
        hasDayChange = message.hasDayChange,
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Part         -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        dayChange = formatDayChange(message),
        combined = if (message.content.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_1),
            contentFormatter.formatPrefix(message.content.senderPrefixes),
            contentFormatter.formatNick(
              message.content.sender,
              self,
              monochromeForeground,
              messageSettings.showHostmaskActions
            )
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_part_2),
            contentFormatter.formatPrefix(message.content.senderPrefixes),
            contentFormatter.formatNick(
              message.content.sender,
              self,
              monochromeForeground,
              messageSettings.showHostmaskActions
            ),
            contentFormatter.formatContent(message.content.content,
                                           monochromeForeground,
                                           message.content.networkId)
          )
        },
        hasDayChange = message.hasDayChange,
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Quit         -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        dayChange = formatDayChange(message),
        combined = if (message.content.content.isBlank()) {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_1),
            contentFormatter.formatPrefix(message.content.senderPrefixes),
            contentFormatter.formatNick(
              message.content.sender,
              self,
              monochromeForeground,
              messageSettings.showHostmaskActions
            )
          )
        } else {
          SpanFormatter.format(
            context.getString(R.string.message_format_quit_2),
            contentFormatter.formatPrefix(message.content.senderPrefixes),
            contentFormatter.formatNick(
              message.content.sender,
              self,
              monochromeForeground,
              messageSettings.showHostmaskActions
            ),
            contentFormatter.formatContent(message.content.content,
                                           monochromeForeground,
                                           message.content.networkId)
          )
        },
        hasDayChange = message.hasDayChange,
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Kick         -> {
        val (user, reason) = message.content.content.split(' ', limit = 2) + listOf("", "")
        FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          dayChange = formatDayChange(message),
          combined = if (reason.isBlank()) {
            SpanFormatter.format(
              context.getString(R.string.message_format_kick_1),
              contentFormatter.formatNick(user, false, monochromeForeground, false),
              contentFormatter.formatPrefix(message.content.senderPrefixes),
              contentFormatter.formatNick(
                message.content.sender,
                self,
                monochromeForeground,
                messageSettings.showHostmaskActions
              )
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_kick_2),
              contentFormatter.formatNick(user, false, monochromeForeground, false),
              contentFormatter.formatPrefix(message.content.senderPrefixes),
              contentFormatter.formatNick(
                message.content.sender,
                self,
                monochromeForeground,
                messageSettings.showHostmaskActions
              ),
              contentFormatter.formatContent(reason,
                                             monochromeForeground,
                                             message.content.networkId)
            )
          },
          hasDayChange = message.hasDayChange,
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
          dayChange = formatDayChange(message),
          combined = if (reason.isBlank()) {
            SpanFormatter.format(
              context.getString(R.string.message_format_kill_1),
              contentFormatter.formatNick(user, false, monochromeForeground, false),
              contentFormatter.formatPrefix(message.content.senderPrefixes),
              contentFormatter.formatNick(
                message.content.sender,
                self,
                monochromeForeground,
                messageSettings.showHostmaskActions
              )
            )
          } else {
            SpanFormatter.format(
              context.getString(R.string.message_format_kill_2),
              contentFormatter.formatNick(user, false, monochromeForeground, false),
              contentFormatter.formatPrefix(message.content.senderPrefixes),
              contentFormatter.formatNick(
                message.content.sender,
                self,
                monochromeForeground,
                messageSettings.showHostmaskActions
              ),
              contentFormatter.formatContent(reason,
                                             monochromeForeground,
                                             message.content.networkId)
            )
          },
          hasDayChange = message.hasDayChange,
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.NetsplitJoin -> {
        val split = message.content.content.split("#:#")
        val (server1, server2) = split.last().split(' ')
        val usersAffected = split.size - 1
        val users = split.subList(0, split.size - 1).map {
          contentFormatter.formatNick(it, false, monochromeForeground, false)
        }

        FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          dayChange = formatDayChange(message),
          combined = context.resources.getQuantityString(
            R.plurals.message_netsplit_join,
            usersAffected,
            server1,
            server2,
            usersAffected,
            users.joinToString(", ")
          ),
          hasDayChange = message.hasDayChange,
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.NetsplitQuit -> {
        val split = message.content.content.split("#:#")
        val (server1, server2) = split.last().split(' ')
        val usersAffected = split.size - 1
        val users = split.subList(0, split.size - 1).map {
          contentFormatter.formatNick(it, false, monochromeForeground, false)
        }

        FormattedMessage(
          id = message.content.messageId,
          time = timeFormatter.format(message.content.time.atZone(zoneId)),
          dayChange = formatDayChange(message),
          combined = context.resources.getQuantityString(
            R.plurals.message_netsplit_quit,
            usersAffected,
            server1,
            server2,
            usersAffected,
            users.joinToString(", ")
          ),
          hasDayChange = message.hasDayChange,
          isMarkerLine = message.isMarkerLine,
          isExpanded = message.isExpanded,
          isSelected = message.isSelected
        )
      }
      Message_Type.Server,
      Message_Type.Info,
      Message_Type.Error        -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        dayChange = formatDayChange(message),
        combined = contentFormatter.formatContent(message.content.content,
                                                  monochromeForeground,
                                                  message.content.networkId),
        hasDayChange = message.hasDayChange,
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.Topic        -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        dayChange = formatDayChange(message),
        combined = contentFormatter.formatContent(message.content.content,
                                                  monochromeForeground,
                                                  message.content.networkId),
        hasDayChange = message.hasDayChange,
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      Message_Type.DayChange    -> FormattedMessage(
        id = message.content.messageId,
        time = "",
        dayChange = formatDayChange(message),
        combined = dateFormatter.format(message.content.time.atZone(zoneId)),
        hasDayChange = message.hasDayChange,
        isMarkerLine = false,
        isExpanded = false,
        isSelected = false
      )
      Message_Type.Invite       -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        dayChange = formatDayChange(message),
        combined = contentFormatter.formatContent(message.content.content,
                                                  monochromeForeground,
                                                  message.content.networkId),
        hasDayChange = message.hasDayChange,
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
      else                      -> FormattedMessage(
        id = message.content.messageId,
        time = timeFormatter.format(message.content.time.atZone(zoneId)),
        dayChange = formatDayChange(message),
        combined = SpanFormatter.format(
          "[%d] %s%s: %s",
          message.content.type.toInt(),
          contentFormatter.formatPrefix(message.content.senderPrefixes),
          contentFormatter.formatNick(
            message.content.sender,
            self,
            monochromeForeground,
            messageSettings.showHostmaskActions
          ),
          message.content.content
        ),
        hasDayChange = message.hasDayChange,
        isMarkerLine = message.isMarkerLine,
        isExpanded = message.isExpanded,
        isSelected = message.isSelected
      )
    }
  }

  private fun formatDayChange(
    message: DisplayMessage) =
    if (message.hasDayChange) dateFormatter.format(message.content.time.atZone(zoneId).truncatedTo(
      ChronoUnit.DAYS)) else null
}
