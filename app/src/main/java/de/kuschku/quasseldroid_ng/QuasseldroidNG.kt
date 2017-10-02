package de.kuschku.quasseldroid_ng

import android.app.Application
import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import de.kuschku.malheur.CrashHandler
import de.kuschku.quasseldroid_ng.util.compatibility.AndroidCompatibilityUtils
import de.kuschku.quasseldroid_ng.util.compatibility.AndroidLoggingHandler
import de.kuschku.quasseldroid_ng.util.compatibility.AndroidStreamChannelFactory
import de.kuschku.quasseldroid_ng.util.helper.systemService

class QuasseldroidNG : Application() {
  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
  }

  override fun onCreate() {
    CrashHandler.init(
      application = this,
      buildConfig = BuildConfig::class.java
    )
    super.onCreate()

    // Init compatibility utils
    AndroidCompatibilityUtils.inject()
    AndroidLoggingHandler.inject()
    AndroidStreamChannelFactory.inject()

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
