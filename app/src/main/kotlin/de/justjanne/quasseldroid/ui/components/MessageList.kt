package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.models.ids.MsgId
import de.justjanne.quasseldroid.sample.SampleMessagesProvider
import de.justjanne.quasseldroid.ui.theme.Typography
import de.justjanne.quasseldroid.util.extensions.OnBottomReached
import de.justjanne.quasseldroid.util.extensions.OnTopReached
import de.justjanne.quasseldroid.util.extensions.getPrevious
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

      val isNew = prev != null &&
        prev.messageId <= markerLine &&
        message.messageId > markerLine

      if (prevDate == null || !messageDate.isEqual(prevDate)) {
        MessageDayChangeView(messageDate, isNew)
      } else if (isNew) {
        NewMessageView()
      }

      MessageBaseView(message, followUp, 32.dp) {
        Text(
          message.content,
          style = Typography.body2,
        )
      }
    }
  }

  listState.OnTopReached(buffer = buffer, onLoadMore = onLoadAtStart)
  listState.OnBottomReached(buffer = buffer, onLoadMore = onLoadAtEnd)
}
