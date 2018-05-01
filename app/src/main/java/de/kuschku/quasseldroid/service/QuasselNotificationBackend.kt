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

package de.kuschku.quasseldroid.service

import android.content.Context
import android.text.SpannableStringBuilder
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.libquassel.session.NotificationManager
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.util.IrcUserUtils
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.util.AvatarHelper
import de.kuschku.quasseldroid.util.NotificationMessage
import de.kuschku.quasseldroid.util.QuasseldroidNotificationManager
import de.kuschku.quasseldroid.util.helper.getColorCompat
import de.kuschku.quasseldroid.util.helper.loadWithFallbacks
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.ui.TextDrawable
import javax.inject.Inject

class QuasselNotificationBackend @Inject constructor(
  private val context: Context,
  private val database: QuasselDatabase,
  private val messageSettings: MessageSettings,
  private val contentFormatter: ContentFormatter,
  private val notificationHandler: QuasseldroidNotificationManager
) : NotificationManager {
  private val senderColors = listOf(
    R.color.senderColor0, R.color.senderColor1, R.color.senderColor2, R.color.senderColor3,
    R.color.senderColor4, R.color.senderColor5, R.color.senderColor6, R.color.senderColor7,
    R.color.senderColor8, R.color.senderColor9, R.color.senderColorA, R.color.senderColorB,
    R.color.senderColorC, R.color.senderColorD, R.color.senderColorE, R.color.senderColorF
  ).map(context::getColorCompat).toIntArray()


  private val selfColor = context.getColorCompat(android.R.color.background_dark)

  override fun init(session: Session) {
    for (buffer in session.bufferSyncer.bufferInfos()) {
      val lastSeenId = session.bufferSyncer.lastSeenMsg(buffer.bufferId)
      database.notifications().markRead(buffer.bufferId, lastSeenId)
    }
  }

  @Synchronized
  override fun processMessages(session: Session, vararg messages: Message) {
    val results = messages.filter {
      it.flag.hasFlag(Message_Flag.Highlight) ||
      it.bufferInfo.type.hasFlag(Buffer_Type.QueryBuffer)
    }.filter {
      val bufferName = it.bufferInfo.bufferName ?: ""
      val networkId = it.bufferInfo.networkId
      val networkName = session.network(networkId)?.networkName() ?: ""

      session.ignoreListManager.match(
        it.content, it.sender, it.type, networkName, bufferName
      ) == IgnoreListManager.StrictnessType.UnmatchedStrictness
    }.filter {
      it.type.hasFlag(Message_Type.Plain) ||
      it.type.hasFlag(Message_Type.Notice) ||
      it.type.hasFlag(Message_Type.Action)
    }.filter {
      !it.flag.hasFlag(Message_Flag.Self)
    }.map {
      QuasselDatabase.NotificationData(
        messageId = it.messageId,
        time = it.time,
        type = it.type,
        flag = it.flag,
        bufferId = it.bufferInfo.bufferId,
        bufferName = it.bufferInfo.bufferName ?: "",
        bufferType = it.bufferInfo.type,
        networkId = it.bufferInfo.networkId,
        sender = it.sender,
        senderPrefixes = it.senderPrefixes,
        realName = it.realName,
        avatarUrl = it.avatarUrl,
        content = it.content
      )
    }
    database.notifications().save(*results.toTypedArray())
    results.map(QuasselDatabase.NotificationData::bufferId).distinct().forEach(this::showNotification)
  }

  @Synchronized
  private fun showNotification(buffer: BufferId) {
    val data = database.notifications().all(buffer)
    data.lastOrNull()?.let {
      val bufferInfo = BufferInfo(
        bufferId = it.bufferId,
        bufferName = it.bufferName,
        type = it.bufferType,
        networkId = it.networkId,
        groupId = 0
      )
      val notification = notificationHandler.notificationGroup(bufferInfo, data.map {
        val nick = SpannableStringBuilder().apply {
          append(contentFormatter.formatPrefix(it.senderPrefixes))
          append(contentFormatter.formatNick(
            it.sender,
            senderColors = senderColors
          ))
        }
        val content = contentFormatter.formatContent(it.content, false)

        val nickName = HostmaskHelper.nick(it.sender)
        val senderColorIndex = IrcUserUtils.senderColor(nickName)
        val rawInitial = nickName.trimStart('-',
                                            '_',
                                            '[',
                                            ']',
                                            '{',
                                            '}',
                                            '|',
                                            '`',
                                            '^',
                                            '.',
                                            '\\')
                           .firstOrNull() ?: nickName.firstOrNull()
        val initial = rawInitial?.toUpperCase().toString()
        val senderColor = if (it.flag.hasFlag(Message_Flag.Self))
          selfColor
        else
          senderColors[senderColorIndex]

        val size = context.resources.getDimensionPixelSize(R.dimen.notification_avatar_width)
        val avatarList = AvatarHelper.avatar(messageSettings, it)
        val avatarResult = GlideApp.with(context).loadWithFallbacks(avatarList)
          ?.optionalCircleCrop()
          ?.placeholder(TextDrawable.builder().buildRound(initial, senderColor))
          ?.submit(size, size)
          ?.get()
        val avatar = avatarResult ?: TextDrawable.builder().buildRound(initial, senderColor)

        NotificationMessage(
          messageId = it.messageId,
          sender = nick,
          content = content,
          time = it.time,
          avatar = avatar
        )
      })
      notificationHandler.notify(notification)
    } ?: notificationHandler.remove(buffer)
  }

  @Synchronized
  override fun clear(buffer: BufferId, lastRead: MsgId) {
    database.notifications().markRead(buffer, lastRead)
    showNotification(buffer)
  }
}
