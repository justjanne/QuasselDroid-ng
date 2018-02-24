package de.kuschku.quasseldroid_ng.util.service

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import de.kuschku.libquassel.session.Backend
import de.kuschku.quasseldroid_ng.Keys
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.settings.data.AppearanceSettings
import de.kuschku.quasseldroid_ng.ui.settings.data.ConnectionSettings
import de.kuschku.quasseldroid_ng.ui.settings.data.Settings
import de.kuschku.quasseldroid_ng.util.helper.updateRecentsHeaderIfExisting

abstract class ServiceBoundActivity : AppCompatActivity() {
  @DrawableRes
  protected val icon: Int = R.mipmap.ic_launcher_recents
  @ColorRes
  protected val recentsHeaderColor: Int = R.color.colorPrimary

  private val connection = BackendServiceConnection()
  protected val backend: LiveData<Backend?>
    get() = connection.backend

  protected lateinit var appearanceSettings: AppearanceSettings
  protected lateinit var connectionSettings: ConnectionSettings
  protected var accountId: Long = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    connection.context = this

    appearanceSettings = Settings.appearance(this)
    connectionSettings = Settings.connection(this)
    accountId = getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
      ?.getLong(Keys.Status.selectedAccount, -1) ?: -1

    setTheme(appearanceSettings.theme.style)
    super.onCreate(savedInstanceState)
    connection.start()
    updateRecentsHeader()
  }

  fun updateRecentsHeader()
    = updateRecentsHeaderIfExisting(title.toString(), icon, recentsHeaderColor)

  override fun setTitle(title: CharSequence?) {
    super.setTitle(title)
    updateRecentsHeader()
  }

  override fun onStart() {
    if (Settings.appearance(this) != appearanceSettings) {
      recreate()
    }
    connection.bind()
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    connection.unbind()
  }

  protected fun stopService() {
    connection.unbind()
    connection.stop()
  }
}
