package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.justjanne.bitflags.of
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.models.flags.MessageType
import de.justjanne.libquassel.protocol.models.ids.MsgId
import de.justjanne.libquassel.protocol.util.irc.HostmaskHelper
import de.justjanne.quasseldroid.R
import de.justjanne.quasseldroid.sample.SampleMessagesProvider
import de.justjanne.quasseldroid.ui.theme.QuasselTheme
import de.justjanne.quasseldroid.ui.theme.Typography
import de.justjanne.quasseldroid.util.extensions.OnBottomReached
import de.justjanne.quasseldroid.util.extensions.OnTopReached
import de.justjanne.quasseldroid.util.extensions.getPrevious
import de.justjanne.quasseldroid.util.format.IrcFormat
import de.justjanne.quasseldroid.util.format.IrcFormatDeserializer
import de.justjanne.quasseldroid.util.format.IrcFormatRenderer
import de.justjanne.quasseldroid.util.format.TextFormatter
import org.threeten.bp.ZoneId

@Preview(name = "Messages", showBackground = true)
@Composable
fun MessageList(
  @PreviewParameter(SampleMessagesProvider::class)
  messages: List<Message>,
  listState: LazyListState = rememberLazyListState(),
  markerLine: MsgId = MsgId(-1),
  buffer: Int = 0,
  onLoadAtStart: () -> Unit = { },
  onLoadAtEnd: () -> Unit = { },
) {
  LazyColumn(state = listState) {
    itemsIndexed(messages, key = { _, item -> item.messageId }) { index, message ->
      val prev = messages.getPrevious(index)
      val prevDate = prev?.time?.atZone(ZoneId.systemDefault())?.toLocalDate()
      val messageDate = message.time.atZone(ZoneId.systemDefault()).toLocalDate()

      val followUp = prev != null &&
        message.sender == prev.sender &&
        message.senderPrefixes == prev.senderPrefixes &&
        message.realName == prev.realName &&
        message.avatarUrl == prev.avatarUrl

      val isNew = (prev == null || prev.messageId <= markerLine) &&
        message.messageId > markerLine

      val parsed = IrcFormatDeserializer.parse(message.content)

      if (prevDate == null || !messageDate.isEqual(prevDate)) {
        MessageDayChangeView(messageDate, isNew)
      } else if (isNew) {
        NewMessageView()
      }

      when (message.type) {
        MessageType.of(MessageType.Plain) -> {
          MessageBase(message, followUp) {
            Text(IrcFormatRenderer.render(parsed), style = Typography.body2)
          }
        }
        MessageType.of(MessageType.Action) -> {
          MessageBaseSmall(message) {
            val nick = HostmaskHelper.nick(message.sender)

            Text(
              TextFormatter.format(
                AnnotatedString(stringResource(R.string.message_format_action)),
                buildNick(nick, message.senderPrefixes),
                IrcFormatRenderer.render(
                  data = parsed.map { it.copy(style = it.style.flipFlag(IrcFormat.Flag.ITALIC)) }
                )
              ),
              style = Typography.body2,
              color = QuasselTheme.chat.onAction
            )
          }
        }
      }
    }
  }

  listState.OnTopReached(buffer = buffer, onLoadMore = onLoadAtStart)
  listState.OnBottomReached(buffer = buffer, onLoadMore = onLoadAtEnd)
}
