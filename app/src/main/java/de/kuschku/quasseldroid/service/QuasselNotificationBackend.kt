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
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.text.SpannableStringBuilder
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.libquassel.session.NotificationManager
import de.kuschku.libquassel.session.Session
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.libquassel.util.irc.SenderColorUtil
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.GlideRequest
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.QuasselDatabase
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.settings.NotificationSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.util.NotificationMessage
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.helper.getColorCompat
import de.kuschku.quasseldroid.util.helper.letIf
import de.kuschku.quasseldroid.util.helper.loadWithFallbacks
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.ui.TextDrawable
import javax.inject.Inject

class QuasselNotificationBackend @Inject constructor(
  private val context: Context,
  private val database: QuasselDatabase,
  private val contentFormatter: ContentFormatter,
  private val notificationHandler: QuasseldroidNotificationManager
) : NotificationManager {
  private var notificationSettings: NotificationSettings
  private var appearanceSettings: AppearanceSettings
  private var messageSettings: MessageSettings

  @ColorInt
  private var selfColor: Int
  private var senderColors: IntArray

  @ColorInt
  private var colorBackground: Int

  init {
    notificationSettings = Settings.notification(context)
    appearanceSettings = Settings.appearance(context)
    messageSettings = Settings.message(context)

    context.setTheme(appearanceSettings.theme.style)
    senderColors = context.theme.styledAttributes(
      R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
      R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
      R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
      R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
    ) {
      IntArray(16) {
        getColor(it, 0)
      }
    }
    selfColor = context.getColorCompat(R.color.material_dark_background)
    colorBackground = context.theme.styledAttributes(R.attr.colorBackground) {
      getColor(0, 0)
    }
  }

  override fun init(session: Session) {
    if (session.features.negotiated.hasFeature(ExtendedFeature.BacklogFilterType)) {
      val buffers = session.bufferSyncer.bufferInfos()
      for (buffer in buffers) {
        val lastSeenId = session.bufferSyncer.lastSeenMsg(buffer.bufferId)
        database.notifications().markRead(buffer.bufferId, lastSeenId)

        val level = buffer.type.let {
          when {
            it hasFlag Buffer_Type.QueryBuffer   -> notificationSettings.query
            it hasFlag Buffer_Type.ChannelBuffer -> notificationSettings.channel
            else                                 -> notificationSettings.other
          }
        }

        when (level) {
          NotificationSettings.Level.ALL       -> {
            val activity = session.bufferSyncer.activity(buffer.bufferId)
            if (activity.hasFlag(Message_Type.Plain) ||
                activity.hasFlag(Message_Type.Action) ||
                activity.hasFlag(Message_Type.Notice))
              session.backlogManager.requestBacklogFiltered(
                buffer.bufferId, lastSeenId, -1, 20, 0,
                Message_Type.of(Message_Type.Plain,
                                Message_Type.Action,
                                Message_Type.Notice).toInt(),
                0
              ) {
                processMessages(session, *it.toTypedArray())
                false
              }
          }
          NotificationSettings.Level.HIGHLIGHT -> {
            val highlightCount = session.bufferSyncer.highlightCount(buffer.bufferId)
            if (highlightCount != 0) {
              session.backlogManager.requestBacklogFiltered(
                buffer.bufferId, lastSeenId, -1, 20, 0,
                Message_Type.of(Message_Type.Plain,
                                Message_Type.Action,
                                Message_Type.Notice).toInt(),
                Message_Flag.of(Message_Flag.Highlight).toInt()
              ) {
                processMessages(session, *it.toTypedArray())
                false
              }
            }
          }
          NotificationSettings.Level.NONE      -> {
            // We don’t want notifications for this type of channel, so we won’t get any.
          }
        }
      }
    }
  }

  fun updateSettings() {
    notificationHandler.updateTranslation()

    notificationSettings = Settings.notification(context)
    appearanceSettings = Settings.appearance(context)
    messageSettings = Settings.message(context)

    context.setTheme(appearanceSettings.theme.style)
    senderColors = context.theme.styledAttributes(
      R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
      R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
      R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
      R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
    ) {
      IntArray(16) {
        getColor(it, 0)
      }
    }
    selfColor = context.getColorCompat(R.color.material_dark_background)
    colorBackground = context.theme.styledAttributes(R.attr.colorBackground) {
      getColor(0, 0)
    }
  }

  @Synchronized
  override fun processMessages(session: Session, vararg messages: Message) {
    val results = messages.filter {
      val level = it.bufferInfo.type.let {
        when {
          it hasFlag Buffer_Type.QueryBuffer   -> notificationSettings.query
          it hasFlag Buffer_Type.ChannelBuffer -> notificationSettings.channel
          else                                 -> notificationSettings.other
        }
      }

      when (level) {
        NotificationSettings.Level.ALL       -> true
        NotificationSettings.Level.HIGHLIGHT -> it.flag.hasFlag(Message_Flag.Highlight)
        NotificationSettings.Level.NONE      -> false
      }
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
    }.filter {
      it.messageId > session.bufferSyncer.lastSeenMsg(it.bufferInfo.bufferId)
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
      val notificationData = data.map {
        val nick = SpannableStringBuilder().apply {
          append(contentFormatter.formatPrefix(it.senderPrefixes))
          append(contentFormatter.formatNick(
            it.sender,
            senderColors = senderColors,
            selfColor = selfColor
          ))
        }
        val content = contentFormatter.formatContent(it.content, false)

        val nickName = HostmaskHelper.nick(it.sender)
        val senderColorIndex = SenderColorUtil.senderColor(nickName)
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
        val senderColor = when (messageSettings.colorizeNicknames) {
          MessageSettings.ColorizeNicknamesMode.ALL          -> senderColors[senderColorIndex]
          MessageSettings.ColorizeNicknamesMode.ALL_BUT_MINE ->
            if (it.flag.hasFlag(Message_Flag.Self)) selfColor
            else senderColors[senderColorIndex]
          MessageSettings.ColorizeNicknamesMode.NONE         -> selfColor
        }

        val size = context.resources.getDimensionPixelSize(R.dimen.notification_avatar_width)
        val radius = context.resources.getDimensionPixelSize(R.dimen.avatar_radius)
        val avatarList = AvatarHelper.avatar(messageSettings, it)
        val avatarResult = try {
          GlideApp.with(context).loadWithFallbacks(avatarList)
            ?.letIf(!messageSettings.squareAvatars, GlideRequest<Drawable>::optionalCircleCrop)
            ?.placeholder(TextDrawable.builder().beginConfig().textColor(colorBackground).endConfig().let {
              if (messageSettings.squareAvatars) it.buildRoundRect(initial, senderColor, radius)
              else it.buildRound(initial, senderColor)
            })
            ?.submit(size, size)
            ?.get()
        } catch (_: Throwable) {
          null
        }
        val avatar = avatarResult
                     ?: TextDrawable.builder().beginConfig().textColor(colorBackground).endConfig().let {
                       if (messageSettings.squareAvatars) it.buildRoundRect(initial,
                                                                            senderColor,
                                                                            radius)
          else it.buildRound(initial, senderColor)
        }

        NotificationMessage(
          messageId = it.messageId,
          sender = nick,
          content = content,
          time = it.time,
          avatar = avatar
        )
      }
      val notification = notificationHandler.notificationMessage(
        notificationSettings, bufferInfo, notificationData
      )
      notificationHandler.notify(notification)
    } ?: notificationHandler.remove(buffer)
  }

  @Synchronized
  override fun clear(buffer: BufferId, lastRead: MsgId) {
    database.notifications().markRead(buffer, lastRead)
    showNotification(buffer)
  }
}
