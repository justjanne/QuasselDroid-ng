package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.util.irc.HostmaskHelper
import de.justjanne.quasseldroid.ui.icons.AvatarIcon
import de.justjanne.quasseldroid.ui.theme.QuasselTheme
import de.justjanne.quasseldroid.ui.theme.Typography
import irc.SenderColorUtil
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

private val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

@Composable
fun MessageBaseView(
  message: Message,
  followUp: Boolean,
  avatarSize: Dp,
  content: @Composable () -> Unit
) {
  val nick = HostmaskHelper.nick(message.sender)
  val senderColor = QuasselTheme.sender.colors[SenderColorUtil.senderColor(nick)]

  Row(modifier = Modifier.padding(2.dp)) {
    if (!followUp) {
      AvatarIcon(nick, null, modifier = Modifier.padding(vertical = 2.dp))
      Spacer(Modifier.width(4.dp))
    } else {
      Spacer(Modifier.width(avatarSize + 8.dp))
    }
    Column {
      if (!followUp) {
        Row {
          Text(
            message.senderPrefixes,
            style = Typography.body2,
            fontWeight = FontWeight.Bold,
          )
          Text(
            nick,
            style = Typography.body2,
            fontWeight = FontWeight.Bold,
            color = senderColor,
          )
          Spacer(Modifier.width(4.dp))
          CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
              message.realName,
              modifier = Modifier.weight(1.0f),
              style = Typography.body2,
              overflow = TextOverflow.Ellipsis,
            )
          }
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
