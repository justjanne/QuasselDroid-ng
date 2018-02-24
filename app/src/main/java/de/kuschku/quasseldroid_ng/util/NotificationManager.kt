package de.kuschku.quasseldroid_ng.util

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.setup.accounts.AccountSelectionActivity
import de.kuschku.quasseldroid_ng.util.helper.editApply
import de.kuschku.quasseldroid_ng.util.helper.sharedPreferences
import de.kuschku.quasseldroid_ng.util.helper.systemService

class NotificationManager(private val context: Context) {
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
          context.getString(R.string.notification_channel_background_title),
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

  fun notificationBackground(): Pair<Int, Notification> {
    val notification = NotificationCompat.Builder(
      context.applicationContext,
      context.getString(R.string.notification_channel_background)
    )
      .setContentIntent(
        PendingIntent.getActivity(
          context.applicationContext, 0,
          Intent(context.applicationContext, AccountSelectionActivity::class.java), 0
        )
      )
      .setContentText(context.getString(R.string.label_running_in_background))
      .setSmallIcon(R.mipmap.ic_launcher_recents)
      .setPriority(NotificationCompat.PRIORITY_MIN)
      .build()

    return BACKGROUND_NOTIFICATION_ID to notification
  }

  companion object {
    val BACKGROUND_NOTIFICATION_ID = Int.MAX_VALUE
  }
}