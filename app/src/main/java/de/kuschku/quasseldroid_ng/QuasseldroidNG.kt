package de.kuschku.quasseldroid_ng

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import de.kuschku.quasseldroid_ng.service.QuasselService
import de.kuschku.quasseldroid_ng.util.AndroidCompatibilityUtils
import de.kuschku.quasseldroid_ng.util.AndroidLoggingHandler
import de.kuschku.quasseldroid_ng.util.AndroidStreamChannelFactory
import de.kuschku.quasseldroid_ng.util.helper.systemService
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.config.ConfigurationBuilder

class QuasseldroidNG : Application() {
  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)

    val config = ConfigurationBuilder(this)
      .setMailTo("support@kuschku.de")
      .setReportingInteractionMode(ReportingInteractionMode.DIALOG)
      .setResDialogText(R.string.crash_text)
      .build()

    ACRA.init(this, config)
  }

  override fun onCreate() {
    super.onCreate()

    if (!ACRA.isACRASenderServiceProcess()) {
      // Init compatibility utils
      AndroidCompatibilityUtils.inject()
      AndroidLoggingHandler.inject()
      AndroidStreamChannelFactory.inject()

      startService(Intent(this, QuasselService::class.java))
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        systemService<ShortcutManager>().dynamicShortcuts = listOf(
          ShortcutInfo.Builder(this, "id1")
            .setShortLabel("#quassel")
            .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_channel))
            .setIntent(packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID))
            .build(),
          ShortcutInfo.Builder(this, "id2")
            .setShortLabel("#quasseldroid")
            .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_channel))
            .setIntent(packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID))
            .build(),
          ShortcutInfo.Builder(this, "id3")
            .setShortLabel("#quassel.de")
            .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_channel))
            .setIntent(packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID))
            .build(),
          ShortcutInfo.Builder(this, "id4")
            .setShortLabel("justJanne")
            .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_query))
            .setIntent(packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID))
            .build()
        )
      }
    }
  }
}
