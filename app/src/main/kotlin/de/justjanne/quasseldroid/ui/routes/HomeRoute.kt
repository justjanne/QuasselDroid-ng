package de.justjanne.quasseldroid.ui.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import de.justjanne.bitflags.of
import de.justjanne.libquassel.protocol.models.BufferInfo
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.models.flags.BufferType
import de.justjanne.libquassel.protocol.models.flags.MessageFlag
import de.justjanne.libquassel.protocol.models.flags.MessageType
import de.justjanne.libquassel.protocol.models.ids.BufferId
import de.justjanne.libquassel.protocol.models.ids.MsgId
import de.justjanne.libquassel.protocol.models.ids.NetworkId
import de.justjanne.libquassel.protocol.util.flatMap
import de.justjanne.libquassel.protocol.util.irc.HostmaskHelper
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.theme.SenderColors
import de.justjanne.quasseldroid.ui.theme.Typography
import irc.SenderColorUtil
import de.justjanne.quasseldroid.util.mapNullable
import de.justjanne.quasseldroid.util.rememberFlow
import de.justjanne.quasseldroid.util.saver.BufferIdSaver
import kotlinx.coroutines.flow.map
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@Composable
fun HomeRoute(backend: QuasselBackend, navController: NavController) {
  val session = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
  }

  val (buffer, setBuffer) = rememberSaveable(stateSaver = BufferIdSaver) {
    mutableStateOf(BufferId(-1))
  }

  val messages: List<Message> = rememberFlow(emptyList()) {
    backend.flow()
      .mapNullable { it.messages }
      .flatMap()
      .mapNullable { it[buffer] }
      .map { it?.messages.orEmpty() }
  }

  val initStatus = rememberFlow(null) {
    backend.flow()
      .mapNullable { it.session }
      .mapNullable { it.baseInitHandler }
      .flatMap()
  }

  val context = LocalContext.current
  Column {
    Text("Side: ${session?.side}")
    if (initStatus != null) {
      val done = initStatus.total - initStatus.waiting.size
      Text("Init: ${initStatus.started} $done/ ${initStatus.total}")
    }
    Button(onClick = { navController.navigate("coreInfo") }) {
      Text("Core Info")
    }
    Button(onClick = {
      backend.disconnect(context)
      navController.navigate("login")
    }) {
      Text("Disconnect")
    }
    LazyColumn {
      items(messages, key = Message::messageId) {
        MessageView(it)
      }
    }
  }
}


private val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

@Preview(name = "Message", showBackground = true)
@Composable
fun MessageView(
  @PreviewParameter(SampleMessageProvider::class)
  message: Message
) {
  val nick = HostmaskHelper.nick(message.sender)
  val senderColor = SenderColors[SenderColorUtil.senderColor(nick)]

  Column {
    Row {
      Text(
        message.senderPrefixes,
        style = Typography.body2,
        fontWeight = FontWeight.Bold
      )
      Text(
        nick,
        style = Typography.body2,
        fontWeight = FontWeight.Bold,
        color = senderColor
      )
      Spacer(Modifier.width(4.dp))
      Text(
        message.realName,
        modifier = Modifier.weight(1.0f),
        style = Typography.body2,
        color = Color(0x8A000000)
      )
    }
    Row {
      Text(
        message.content,
        modifier = Modifier.weight(1.0f),
        style = Typography.body2
      )
      Text(
        message.time
          .atZone(ZoneId.systemDefault())
          .format(formatter),
        style = Typography.body2,
        color = Color(0x8A000000),
        fontSize = 12.sp
      )
    }
  }
}

class SampleMessageProvider : PreviewParameterProvider<Message> {
  override val values = sequenceOf(
    Message(
      messageId = MsgId(108062924),
      bufferInfo = BufferInfo(
        bufferId = BufferId(3746),
        bufferName = "#quasseldroid",
        networkId = NetworkId(4),
        type = BufferType.of(BufferType.Channel)
      ),
      time = Instant.parse("2022-02-20T18:24:48.891Z"),
      type = MessageType.of(MessageType.Quit),
      sender = "CrazyBonz!~CrazyBonz@user/CrazyBonz",
      senderPrefixes = "",
      avatarUrl = "",
      realName = "CrazyBonz",
      content = "#quasseldroid",
      flag = MessageFlag.of()
    ),
    Message(
      messageId = MsgId(108063975),
      bufferInfo = BufferInfo(
        bufferId = BufferId(3746),
        bufferName = "#quasseldroid",
        networkId = NetworkId(4),
        type = BufferType.of(BufferType.Channel)
      ),
      time = Instant.parse("2022-02-20T19:56:01.588Z"),
      type = MessageType.of(MessageType.Plain),
      sender = "winch!~AdminUser@185.14.29.13",
      senderPrefixes = "",
      avatarUrl = "",
      realName = "Wincher,,,",
      content = "Can i script some actions like in mIRC?",
      flag = MessageFlag.of()
    ),
    Message(
      messageId = MsgId(108064014),
      bufferInfo = BufferInfo(
        bufferId = BufferId(3746),
        bufferName = "#quasseldroid",
        networkId = NetworkId(4),
        type = BufferType.of(BufferType.Channel)
      ),
      time = Instant.parse("2022-02-20T20:06:39.159Z"),
      type = MessageType.of(MessageType.Quit),
      sender = "mavhq!~quassel@mapp-14-b2-v4wan-161519-cust401.vm15.cable.virginm.net",
      senderPrefixes = "",
      avatarUrl = "",
      realName = "mavhc",
      content = "Quit: http://quassel-irc.org - Chat comfortably. Anywhere.",
      flag = MessageFlag.of()
    ),
    Message(
      messageId = MsgId(108064022),
      bufferInfo = BufferInfo(
        bufferId = BufferId(3746),
        bufferName = "#quasseldroid",
        networkId = NetworkId(4),
        type = BufferType.of(BufferType.Channel)
      ),
      time = Instant.parse("2022-02-20T20:07:13.45Z"),
      type = MessageType.of(MessageType.Join),
      sender = "mavhq!~quassel@mapp-14-b2-v4wan-161519-cust401.vm15.cable.virginm.net",
      senderPrefixes = "",
      avatarUrl = "",
      realName = "mavhc",
      content = "#quasseldroid",
      flag = MessageFlag.of()
    )
  )
}
