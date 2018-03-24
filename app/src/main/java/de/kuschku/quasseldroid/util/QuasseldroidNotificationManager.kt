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
import de.kuschku.quasseldroid.util.helper.sharedPreferences
import de.kuschku.quasseldroid.util.helper.systemService

class QuasseldroidNotificationManager(private val context: Context) {
  fun init() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
      prepareChannels()
  }

  @TargetApi(Build.VERSION_CODES.O)
  private fun prepareChannels() {
    val notificationManager = context.systemService<NotificationManager>()
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
    val intentOpen = Intent(context.applicationContext, ChatActivity::class.java)
    intentOpen.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    val pendingIntentOpen = PendingIntent.getActivity(context.applicationContext, 0, intentOpen, 0)

    val intentDisconnect = Intent(context, QuasselService::class.java)
    intentDisconnect.putExtra("disconnect", true)
    val pendingIntentDisconnect = PendingIntent.getService(
      context, 0, intentDisconnect, PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(
      context.applicationContext,
      context.getString(R.string.notification_channel_background)
    )
      .setContentIntent(pendingIntentOpen)
      .addAction(0, context.getString(R.string.label_open), pendingIntentOpen)
      .addAction(0, context.getString(R.string.label_disconnect), pendingIntentDisconnect)
      .setSmallIcon(R.drawable.ic_logo)
      .setColor(context.resources.getColor(R.color.colorPrimary))
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
    val BACKGROUND_NOTIFICATION_ID = Int.MAX_VALUE
  }

  data class Handle(
    val id: Int,
    val builder: NotificationCompat.Builder
  )
}