/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.types

import org.threeten.bp.Instant

data class Message(
  val messageId: MsgId,
  val time: Instant,
  val type: MessageTypes,
  val flag: MessageFlags,
  val bufferInfo: BufferInfo,
  val sender: String,
  val senderPrefixes: String,
  val realName: String,
  val avatarUrl: String,
  val content: String
) {
  override fun toString(): String {
    return "Message(messageId=$messageId, time=$time, type=$type, flag=$flag, bufferInfo=$bufferInfo, sender='$sender', senderPrefixes='$senderPrefixes', content='$content')"
  }
}
