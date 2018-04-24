/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.service.QuasselService
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.util.helper.editApply
import de.kuschku.quasseldroid.util.helper.getColorCompat
import de.kuschku.quasseldroid.util.helper.sharedPreferences

class QuasseldroidNotificationManager(private val context: Context) {
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
          context.getString(R.string.notification_channel_background),
          context.getString(R.string.notification_channel_connection_title),
          NotificationManager.IMPORTANCE_MIN
        ),
        NotificationChannel(
          context.getString(R.string.notification_channel_highlight),
          context.getString(R.string.notification_channel_highlight_title),
          NotificationManager.IMPORTANCE_HIGH
        )
      )
    )
  }

  private fun id(): Int = context.sharedPreferences {
    val key = context.getString(R.string.preference_notification_id_key)
    val id = getInt(key, 1) + 1
    editApply {
      putInt(key, id)
    }
    id
  }

  fun notificationBackground(): Handle {
    val pendingIntentOpen = PendingIntent.getActivity(
      context.applicationContext,
      0,
      ChatActivity.intent(context.applicationContext).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
      },
      0
    )

    val pendingIntentDisconnect = PendingIntent.getService(
      context,
      0,
      QuasselService.intent(context.applicationContext, disconnect = true),
      PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(
      context.applicationContext,
      context.getString(R.string.notification_channel_background)
    )
      .setContentIntent(pendingIntentOpen)
      .addAction(0, context.getString(R.string.label_open), pendingIntentOpen)
      .addAction(0, context.getString(R.string.label_disconnect), pendingIntentDisconnect)
      .setSmallIcon(R.mipmap.ic_logo)
      .setColor(context.getColorCompat(R.color.colorPrimary))
      .setPriority(NotificationCompat.PRIORITY_MIN)
    return Handle(BACKGROUND_NOTIFICATION_ID, notification)
  }

  fun notify(handle: Handle) {
    NotificationManagerCompat.from(context).notify(handle.id, handle.builder.build())
  }

  fun remove(handle: Handle) {
    NotificationManagerCompat.from(context).cancel(handle.id)
  }

  companion object {
    const val BACKGROUND_NOTIFICATION_ID = Int.MAX_VALUE
  }

  data class Handle(
    val id: Int,
    val builder: NotificationCompat.Builder
  )
}
