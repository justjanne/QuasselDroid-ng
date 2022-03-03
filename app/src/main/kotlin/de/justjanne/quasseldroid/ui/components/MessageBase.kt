package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
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
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.util.irc.HostmaskHelper
import de.justjanne.quasseldroid.sample.SampleMessageProvider
import de.justjanne.quasseldroid.ui.icons.AvatarIcon
import de.justjanne.quasseldroid.ui.theme.QuasselTheme
import de.justjanne.quasseldroid.ui.theme.Typography
import irc.SenderColorUtil
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

private val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

@Composable
fun buildNick(nick: String, senderPrefixes: String): AnnotatedString {
  val senderColor = QuasselTheme.sender.colors[SenderColorUtil.senderColor(nick)]

  return buildAnnotatedString {
    if (senderPrefixes.isNotEmpty()) {
      append(senderPrefixes)
    }
    pushStyle(SpanStyle(color = senderColor, fontWeight = FontWeight.Bold))
    append(nick)
    pop()
  }
}

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
        Row {
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
      }
      Row {
        Box(modifier = Modifier.weight(1.0f)) {
          content()
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
          Text(
            message.time
              .atZone(ZoneId.systemDefault())
              .format(formatter),
            style = Typography.body2,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Bottom)
          )
        }
      }
    }
  }
}

@Preview(name = "Message Small", showBackground = true)
@Composable
fun MessageBaseSmall(
  @PreviewParameter(SampleMessageProvider::class)
  message: Message,
  followUp: Boolean = false,
  // avatarSize: Dp = 32.dp,
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
          .format(formatter),
        style = Typography.body2,
        fontSize = 12.sp,
        modifier = Modifier.align(Alignment.Bottom)
      )
    }
  }
}
