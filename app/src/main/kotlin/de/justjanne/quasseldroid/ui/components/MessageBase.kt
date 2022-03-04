package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.justjanne.libquassel.irc.HostmaskHelper
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.quasseldroid.sample.SampleMessageProvider
import de.justjanne.quasseldroid.ui.Constants
import de.justjanne.quasseldroid.ui.icons.AvatarIcon
import de.justjanne.quasseldroid.ui.theme.Typography
import org.threeten.bp.ZoneId

@Preview(name = "Message Base", showBackground = true)
@Composable
fun MessageBase(
  @PreviewParameter(SampleMessageProvider::class)
  message: Message,
  followUp: Boolean = false,
  // avatarSize: Dp = 32.dp
  content: @Composable () -> Unit = { Text(message.content, style = Typography.body2) }
) {
  val avatarSize = 32.dp

  val nick = HostmaskHelper.nick(message.sender)

  Row(
    modifier = Modifier
      .padding(2.dp)
      .fillMaxWidth()
  ) {
    if (!followUp) {
      Spacer(Modifier.width(4.dp))
      AvatarIcon(
        nick,
        size = avatarSize,
        modifier = Modifier
          .paddingFromBaseline(top = 28.sp)
      )
      Spacer(Modifier.width(4.dp))
    } else {
      Spacer(Modifier.width(avatarSize + 8.dp))
    }
    Column(modifier = Modifier.align(Alignment.CenterVertically)) {
      if (!followUp) {
        Text(
          buildAnnotatedString {
            append(buildNick(nick, message.senderPrefixes))
            append(' ')
            pushStyle(SpanStyle(color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)))
            append(message.realName)
            pop()
          },
          style = Typography.body2
        )
      }
      Row {
        Box(modifier = Modifier.weight(1.0f)) {
          content()
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
          Text(
            message.time
              .atZone(ZoneId.systemDefault())
              .format(Constants.timeFormatter),
            style = Typography.body2,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Bottom)
          )
        }
      }
    }
  }
}
