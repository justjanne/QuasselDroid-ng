package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.irc.HostmaskHelper
import de.justjanne.quasseldroid.sample.SampleMessageProvider
import de.justjanne.quasseldroid.ui.Constants
import de.justjanne.quasseldroid.ui.icons.AvatarIcon
import de.justjanne.quasseldroid.ui.theme.Typography
import org.threeten.bp.ZoneId

@Preview(name = "Message Small", showBackground = true)
@Composable
fun MessageBaseSmall(
  @PreviewParameter(SampleMessageProvider::class)
  message: Message,
  content: @Composable () -> Unit = {
    val nick = HostmaskHelper.nick(message.sender)

    Text(buildAnnotatedString {
      append("— ")
      append(buildNick(nick, message.senderPrefixes))
      append(" ")
      append(message.content)
    }, style = Typography.body2)
  }
) {
  val avatarSize = 16.dp
  val nick = HostmaskHelper.nick(message.sender)

  Row(
    modifier = Modifier
      .padding(2.dp)
      .fillMaxWidth()
  ) {
    Spacer(Modifier.width(20.dp))
    AvatarIcon(
      nick,
      modifier = Modifier
        .align(Alignment.Top)
        .paddingFromBaseline(top = 14.sp),
      size = avatarSize
    )
    Spacer(Modifier.width(4.dp))
    Box(modifier = Modifier.weight(1.0f)) {
      content()
    }
    Spacer(Modifier.width(4.dp))
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
