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

package de.kuschku.libquassel.protocol

import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.util.flag.Flag
import de.kuschku.libquassel.util.flag.Flags
import org.threeten.bp.Instant

data class Message(
  val messageId: MsgId,
  val time: Instant,
  val type: Message_Types,
  val flag: Message_Flags,
  val bufferInfo: BufferInfo,
  val sender: String,
  val senderPrefixes: String,
  val realName: String,
  val avatarUrl: String,
  val content: String
) {
  enum class MessageType(override val bit: Int) :
    Flag<MessageType> {
    Plain(0x00001),
    Notice(0x00002),
    Action(0x00004),
    Nick(0x00008),
    Mode(0x00010),
    Join(0x00020),
    Part(0x00040),
    Quit(0x00080),
    Kick(0x00100),
    Kill(0x00200),
    Server(0x00400),
    Info(0x00800),
    Error(0x01000),
    DayChange(0x02000),
    Topic(0x04000),
    NetsplitJoin(0x08000),
    NetsplitQuit(0x10000),
    Invite(0x20000),
    Markerline(0x40000);

    companion object : Flags.Factory<MessageType> {
      override val NONE = MessageType.of()
      override fun of(bit: Int) = Flags.of<MessageType>(bit)
      override fun of(vararg flags: MessageType) = Flags.of(*flags)
      override fun of(flags: Iterable<MessageType>) = Flags.of(flags)
    }
  }

  enum class MessageFlag(override val bit: Int) :
    Flag<MessageFlag> {
    Self(0x01),
    Highlight(0x02),
    Redirected(0x04),
    ServerMsg(0x08),
    Backlog(0x80);

    companion object : Flags.Factory<MessageFlag> {
      override val NONE = MessageFlag.of()
      override fun of(bit: Int) = Flags.of<MessageFlag>(bit)
      override fun of(vararg flags: MessageFlag) = Flags.of(*flags)
      override fun of(flags: Iterable<MessageFlag>) = Flags.of(flags)
    }
  }


  override fun toString(): String {
    return "Message(messageId=$messageId, time=$time, type=$type, flag=$flag, bufferInfo=$bufferInfo, sender='$sender', senderPrefixes='$senderPrefixes', content='$content')"
  }
}
