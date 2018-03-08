package de.kuschku.quasseldroid_ng.util.service

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import de.kuschku.libquassel.session.Backend
import de.kuschku.quasseldroid_ng.Keys
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.settings.AppearanceSettings
import de.kuschku.quasseldroid_ng.settings.ConnectionSettings
import de.kuschku.quasseldroid_ng.settings.Settings
import de.kuschku.quasseldroid_ng.ui.setup.accounts.AccountSelectionActivity
import de.kuschku.quasseldroid_ng.util.helper.sharedPreferences
import de.kuschku.quasseldroid_ng.util.helper.updateRecentsHeaderIfExisting

abstract class ServiceBoundActivity : AppCompatActivity(),
                                      SharedPreferences.OnSharedPreferenceChangeListener {
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

  private var startedSelection = false

  override fun onCreate(savedInstanceState: Bundle?) {
    connection.context = this

    appearanceSettings = Settings.appearance(this)
    connectionSettings = Settings.connection(this)

    checkConnection()

    setTheme(appearanceSettings.theme.style)
    super.onCreate(savedInstanceState)
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
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      registerOnSharedPreferenceChangeListener(this@ServiceBoundActivity)
    }
    connection.bind()
    checkConnection()
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    connection.unbind()
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      unregisterOnSharedPreferenceChangeListener(this@ServiceBoundActivity)
    }
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    checkConnection()
  }

  private fun checkConnection() {
    accountId = getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
      ?.getLong(Keys.Status.selectedAccount, -1) ?: -1

    if (!sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
        getBoolean(Keys.Status.reconnect, false)
      } || accountId == -1L) {

      if (!startedSelection) {
        startActivityForResult(
          Intent(this, AccountSelectionActivity::class.java), REQUEST_SELECT_ACCOUNT
        )
        startedSelection = true
      }
    } else {
      connection.start()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_SELECT_ACCOUNT) {
      startedSelection = false

      if (resultCode == Activity.RESULT_CANCELED) {
        finish()
      }
    }
  }

  protected fun stopService() {
    connection.unbind()
    connection.stop()
  }

  companion object {
    const val REQUEST_SELECT_ACCOUNT = 0
  }
}
