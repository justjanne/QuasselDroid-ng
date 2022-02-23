/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.quasseldroid.service

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.session.NotificationManager
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.libquassel.util.helper.clampOf
import de.kuschku.libquassel.util.helper.or
import de.kuschku.libquassel.util.irc.HostmaskHelper
import de.kuschku.libquassel.util.irc.SenderColorUtil
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.GlideRequest
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.persistence.dao.all
import de.kuschku.quasseldroid.persistence.dao.buffers
import de.kuschku.quasseldroid.persistence.dao.markRead
import de.kuschku.quasseldroid.persistence.db.QuasselDatabase
import de.kuschku.quasseldroid.persistence.models.NotificationData
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.settings.NotificationSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.util.NotificationBuffer
import de.kuschku.quasseldroid.util.NotificationMessage
import de.kuschku.quasseldroid.util.avatars.AvatarHelper
import de.kuschku.quasseldroid.util.helper.getColorCompat
import de.kuschku.quasseldroid.util.helper.letIf
import de.kuschku.quasseldroid.util.helper.loadWithFallbacks
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.ui.drawable.TextDrawable
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper.Companion.IGNORED_CHARS
import org.threeten.bp.Instant
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
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
  private val executor = Executors.newSingleThreadScheduledExecutor()

  @ColorInt
  private var selfColor: Int
  private var senderColors: IntArray

  @ColorInt
  private var colorBackground: Int

  private var initTime: Instant = Instant.EPOCH

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

  override fun init(session: ISession) {
    initTime = Instant.now()
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
                buffer.bufferId, lastSeenId, MsgId(-1), 20, 0,
                Message_Type.of(Message_Type.Plain,
                                Message_Type.Action,
                                Message_Type.Notice).toInt(),
                0
              ) {
                processMessages(session, true, *it.toTypedArray())
                false
              }
          }
          NotificationSettings.Level.HIGHLIGHT -> {
            val highlightCount = session.bufferSyncer.highlightCount(buffer.bufferId)
            if (highlightCount != 0) {
              session.backlogManager.requestBacklogFiltered(
                buffer.bufferId, lastSeenId, MsgId(-1), 20, 0,
                Message_Type.of(Message_Type.Plain,
                                Message_Type.Action,
                                Message_Type.Notice).toInt(),
                Message_Flag.of(Message_Flag.Highlight).toInt()
              ) {
                processMessages(session, true, *it.toTypedArray())
                false
              }
            }
          }
          NotificationSettings.Level.NONE      -> {
            // We don’t want notifications for this type of channel, so we won’t get any.
          }
        }
      }

      // Cleanup
      val removedBuffers = database.notifications().buffers().minus(buffers.map(BufferInfo::bufferId))
      for (removedBuffer in removedBuffers) {
        database.notifications().markRead(removedBuffer, MsgId.MAX_VALUE)
      }

      // Update notifications to have actions
      showConnectedNotifications()
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
  override fun processMessages(session: ISession, show: Boolean, vararg messages: Message) {
    val now = Instant.now()
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
      val network = session.network(it.bufferInfo.networkId)
      val me = network?.me()
      NotificationData.of(
        messageId = it.messageId,
        creationTime = now,
        time = it.time,
        type = it.type,
        flag = it.flag,
        bufferId = it.bufferInfo.bufferId,
        bufferName = it.bufferInfo.bufferName ?: "",
        bufferType = it.bufferInfo.type,
        networkId = it.bufferInfo.networkId,
        networkName = network?.networkName() ?: "",
        sender = it.sender,
        senderPrefixes = it.senderPrefixes,
        realName = it.realName,
        avatarUrl = it.avatarUrl,
        content = it.content,
        ownNick = me?.nick() ?: "",
        ownIdent = me?.user() ?: "",
        ownRealName = me?.realName() ?: "",
        ownAvatarUrl = "",
        hidden = false
      )
    }
    database.notifications().save(*results.toTypedArray())
    if (show) {
      executor.schedule(
        {
          for (buffer in results.map(NotificationData::bufferId).distinct()) {
            this.showNotification(buffer)
          }
        },
        clampOf(session.lag.or(0) * 3 + 100, 16, 3_000),
        TimeUnit.MILLISECONDS
      )
    }
  }

  fun showConnectedNotifications() {
    for (buffer in database.notifications().buffers()) {
      this.showNotification(buffer, true)
    }
  }

  fun showDisconnectedNotifications() {
    for (buffer in database.notifications().buffers()) {
      this.showNotification(buffer, false)
    }
  }

  @Synchronized
  private fun showNotification(buffer: BufferId, isConnected: Boolean = true) {
    val data = database.notifications().all(buffer)
    data.lastOrNull()?.let {
      // Only send a loud notification if it has any new messages
      val max = data.maxByOrNull { it.creationTime }
      val isLoud = max?.creationTime?.isAfter(initTime) == true

      val buffer = NotificationBuffer(
        id = it.bufferId,
        name = it.bufferName,
        type = it.bufferType,
        networkName = it.networkName
      )

      val size = context.resources.getDimensionPixelSize(R.dimen.notification_avatar_width)
      val radius = context.resources.getDimensionPixelSize(R.dimen.avatar_radius)
      val unknownSenderLabel = context.getString(R.string.label_unknown_sender)

      fun obtainAvatar(nickName: String, ident: String, realName: String, avatarUrl: String,
                       self: Boolean): Drawable {
        val senderColorIndex = SenderColorUtil.senderColor(nickName)
        val rawInitial = nickName.trimStart(*IGNORED_CHARS)
                           .firstOrNull() ?: nickName.firstOrNull()
        val initial = rawInitial?.uppercase().toString()
        val senderColor = when (messageSettings.colorizeNicknames) {
          MessageSettings.SenderColorMode.ALL          -> senderColors[senderColorIndex]
          MessageSettings.SenderColorMode.ALL_BUT_MINE ->
            if (self) selfColor
            else senderColors[senderColorIndex]
          MessageSettings.SenderColorMode.NONE         -> selfColor
        }

        val avatarList = AvatarHelper.avatar(messageSettings, ident, realName, avatarUrl, size)
        val avatarResult = try {
          GlideApp.with(context).loadWithFallbacks(avatarList)
            ?.letIf(!messageSettings.squareAvatars, GlideRequest<Drawable>::optionalCircleCrop)
            ?.placeholder(TextDrawable.builder().beginConfig()
                            .textColor((colorBackground and 0xFFFFFF) or (0x8A shl 24)).useFont(
                Typeface.DEFAULT_BOLD).endConfig().let {
                if (messageSettings.squareAvatars) it.buildRoundRect(initial, senderColor, radius)
                else it.buildRound(initial, senderColor)
              })
            ?.submit(size, size)
            ?.get()
        } catch (_: Throwable) {
          null
        }
        return avatarResult ?: TextDrawable.builder().beginConfig()
          .textColor((colorBackground and 0xFFFFFF) or (0x8A shl 24)).useFont(Typeface.DEFAULT_BOLD).endConfig().let {
            if (messageSettings.squareAvatars) it.buildRoundRect(initial, senderColor, radius)
            else it.buildRound(initial, senderColor)
          }
      }

      val selfInfo = SelfInfo(
        nick = if (it.ownNick.isNotEmpty()) it.ownNick else unknownSenderLabel,
        avatar = obtainAvatar(
          it.ownNick,
          it.ownIdent,
          it.ownRealName,
          it.ownAvatarUrl,
          true
        )
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
        val (content, _) = contentFormatter.formatContent(it.content, false, false, it.networkId)

        NotificationMessage(
          messageId = it.messageId,
          fullSender = it.sender,
          sender = if (nick.isNotEmpty()) nick else unknownSenderLabel,
          content = content,
          time = it.time,
          avatar = obtainAvatar(
            HostmaskHelper.nick(it.sender),
            HostmaskHelper.user(it.sender),
            it.realName,
            it.avatarUrl,
            it.flag.hasFlag(Message_Flag.Self)
          )
        )
      }
      val notification = notificationHandler.notificationMessage(
        notificationSettings, buffer, selfInfo, notificationData, isLoud, isConnected
      )
      notificationHandler.notify(notification)
    } ?: notificationHandler.remove(buffer.id)
  }

  @Synchronized
  override fun clear(buffer: BufferId, lastRead: MsgId) {
    database.notifications().markRead(buffer, lastRead)
    showNotification(buffer)
  }
}
