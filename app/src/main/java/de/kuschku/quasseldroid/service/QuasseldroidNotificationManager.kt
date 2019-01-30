/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.util.flag.hasFlag
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.NotificationSettings
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.NotificationBuffer
import de.kuschku.quasseldroid.util.NotificationMessage
import de.kuschku.quasseldroid.util.helper.getColorCompat
import de.kuschku.quasseldroid.util.helper.letIf
import de.kuschku.quasseldroid.util.ui.LocaleHelper
import javax.inject.Inject

class QuasseldroidNotificationManager @Inject constructor(private val context: Context) {
  private val notificationManagerCompat = NotificationManagerCompat.from(context)
  private var translatedLocale: Context = LocaleHelper.setLocale(context)

  fun updateTranslation() {
    translatedLocale = LocaleHelper.setLocale(context)
  }

  fun init() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
      prepareChannels()
  }

  @TargetApi(Build.VERSION_CODES.O)
  private fun prepareChannels() {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannels(
      listOf(
        NotificationChannel(
          translatedLocale.getString(R.string.notification_channel_background),
          translatedLocale.getString(R.string.notification_channel_connection_title),
          NotificationManager.IMPORTANCE_LOW
        ),
        NotificationChannel(
          translatedLocale.getString(R.string.notification_channel_highlight),
          translatedLocale.getString(R.string.notification_channel_highlight_title),
          NotificationManager.IMPORTANCE_HIGH
        ).apply {
          enableLights(true)
          enableVibration(true)
          lightColor = context.getColorCompat(R.color.colorPrimary)
          lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        },
        NotificationChannel(
          translatedLocale.getString(R.string.notification_channel_old_highlight),
          translatedLocale.getString(R.string.notification_channel_old_highlight_title),
          NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
          setSound(null, null)
          enableLights(true)
          enableVibration(false)
          lightColor = context.getColorCompat(R.color.colorPrimary)
          lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
      )
    )
  }

  private fun bitmapFromDrawable(drawable: Drawable): Bitmap {
    val bitmap = Bitmap.createBitmap(
      context.resources.getDimensionPixelSize(R.dimen.notification_avatar_width),
      context.resources.getDimensionPixelSize(R.dimen.notification_avatar_height),
      Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
  }

  fun notificationMessage(notificationSettings: NotificationSettings, buffer: NotificationBuffer,
                          selfInfo: SelfInfo, notifications: List<NotificationMessage>,
                          isLoud: Boolean, isConnected: Boolean): Handle {
    val pendingIntentOpen = PendingIntent.getActivity(
      context.applicationContext,
      System.currentTimeMillis().toInt(),
      ChatActivity.intent(context.applicationContext, bufferId = buffer.id).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
      },
      0
    )

    val remoteInput = RemoteInput.Builder("reply_content")
      .setLabel(translatedLocale.getString(R.string.label_reply))
      .build()

    val replyPendingIntent = PendingIntent.getService(
      context.applicationContext,
      System.currentTimeMillis().toInt(),
      QuasselService.intent(
        context,
        bufferId = buffer.id
      ),
      0
    )

    val markReadPendingIntent = PendingIntent.getService(
      context.applicationContext,
      System.currentTimeMillis().toInt(),
      QuasselService.intent(
        context,
        bufferId = buffer.id,
        markReadMessage = notifications.last().messageId
      ),
      0
    )

    val deletePendingIntent = PendingIntent.getService(
      context.applicationContext,
      System.currentTimeMillis().toInt(),
      QuasselService.intent(
        context,
        bufferId = buffer.id,
        hideMessage = notifications.last().messageId
      ),
      0
    )

    val notification = NotificationCompat.Builder(
      context.applicationContext,
      translatedLocale.getString(
        if (isLoud) R.string.notification_channel_highlight
        else R.string.notification_channel_old_highlight
      )
    )
      .setContentIntent(pendingIntentOpen)
      .setDeleteIntent(deletePendingIntent)
      .setSmallIcon(R.mipmap.ic_logo)
      .setColor(context.getColorCompat(R.color.colorPrimary))
      .setLights(context.getColorCompat(R.color.colorPrimary), 200, 200)
      .apply {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
          var defaults = 0
          if (isLoud) {
            if (!notificationSettings.sound.isEmpty()) {
              setSound(Uri.parse(notificationSettings.sound))
            }
            if (notificationSettings.vibrate) {
              defaults = defaults or NotificationCompat.DEFAULT_VIBRATE
            }
          }
          if (notificationSettings.light) {
            defaults = defaults or NotificationCompat.DEFAULT_LIGHTS
          }
          setDefaults(defaults)
        }
      }
      .setCategory(NotificationCompat.CATEGORY_MESSAGE)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setStyle(
        NotificationCompat.MessagingStyle(
          Person.Builder()
            .setKey("")
            .setName(selfInfo.nick)
            .setIcon(IconCompat.createWithBitmap(bitmapFromDrawable(selfInfo.avatar)))
            .build()
        )
          .setGroupConversation(!buffer.type.hasFlag(Buffer_Type.QueryBuffer))
          .setConversationTitle(
            if (notificationSettings.networkNameInNotificationTitle) "${buffer.name} — ${buffer.networkName}"
            else buffer.name
          )
          .also {
            for (notification in notifications) {
              it.addMessage(
                notification.content,
                notification.time.toEpochMilli(),
                Person.Builder()
                  .setKey(notification.fullSender)
                  .setName(notification.sender)
                  .setIcon(IconCompat.createWithBitmap(bitmapFromDrawable(notification.avatar)))
                  .build()
              )
            }
          }
      )
      .letIf(isConnected) {
        it.addAction(0, translatedLocale.getString(R.string.label_mark_read), markReadPendingIntent)
          .letIf(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            it.addAction(
              NotificationCompat.Action.Builder(
                0,
                translatedLocale.getString(R.string.label_reply),
                replyPendingIntent
              ).addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build()
            )
          }
      }
      .setWhen(notifications.last().time.toEpochMilli())
      .apply {
        if (buffer.type.hasFlag(Buffer_Type.QueryBuffer)) {
          notifications.lastOrNull()?.avatar?.let {
            setLargeIcon(bitmapFromDrawable(it))
          }
        }
      }
    return Handle(buffer.id.id, notification)
  }

  fun notificationBackground(): Handle {
    val pendingIntentOpen = PendingIntent.getActivity(
      context.applicationContext,
      System.currentTimeMillis().toInt(),
      ChatActivity.intent(context.applicationContext).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
      },
      0
    )

    val pendingIntentDisconnect = PendingIntent.getService(
      context,
      System.currentTimeMillis().toInt(),
      QuasselService.intent(context.applicationContext, disconnect = true),
      0
    )

    val notification = NotificationCompat.Builder(
      context.applicationContext,
      translatedLocale.getString(R.string.notification_channel_background)
    )
      .setContentIntent(pendingIntentOpen)
      .addAction(0, translatedLocale.getString(R.string.label_open), pendingIntentOpen)
      .addAction(0, translatedLocale.getString(R.string.label_disconnect), pendingIntentDisconnect)
      .setSmallIcon(R.mipmap.ic_logo)
      .setColor(context.getColorCompat(R.color.colorPrimary))
      .setPriority(NotificationCompat.PRIORITY_MIN)
    return Handle(BACKGROUND_NOTIFICATION_ID, notification)
  }

  fun notify(handle: Handle) {
    notificationManagerCompat.notify(handle.id, handle.builder.build())
  }

  fun remove(handle: Handle) {
    notificationManagerCompat.cancel(handle.id)
  }

  fun remove(id: Int) {
    notificationManagerCompat.cancel(id)
  }

  companion object {
    const val BACKGROUND_NOTIFICATION_ID = Int.MAX_VALUE
  }

  data class Handle(
    val id: Int,
    val builder: NotificationCompat.Builder
  )
}
