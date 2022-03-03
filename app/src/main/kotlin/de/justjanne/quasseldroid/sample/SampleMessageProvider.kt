package de.justjanne.quasseldroid.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.justjanne.bitflags.of
import de.justjanne.libquassel.protocol.models.BufferInfo
import de.justjanne.libquassel.protocol.models.Message
import de.justjanne.libquassel.protocol.models.flags.BufferType
import de.justjanne.libquassel.protocol.models.flags.MessageFlag
import de.justjanne.libquassel.protocol.models.flags.MessageType
import de.justjanne.libquassel.protocol.models.ids.BufferId
import de.justjanne.libquassel.protocol.models.ids.MsgId
import de.justjanne.libquassel.protocol.models.ids.NetworkId
import de.justjanne.libquassel.protocol.util.irc.HostmaskHelper
import org.threeten.bp.Instant

class SampleNickProvider : PreviewParameterProvider<String> {
  override val values = SampleMessageProvider().values.map { HostmaskHelper.nick(it.sender) }
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

