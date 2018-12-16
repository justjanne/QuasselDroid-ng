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

package de.kuschku.quasseldroid.util.service

import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.ConnectionSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.util.helper.editCommit
import de.kuschku.quasseldroid.util.helper.sharedPreferences
import de.kuschku.quasseldroid.util.helper.updateRecentsHeaderIfExisting
import de.kuschku.quasseldroid.util.ui.ThemedActivity
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

abstract class ServiceBoundActivity :
  ThemedActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
  @DrawableRes
  protected val icon: Int = R.mipmap.ic_launcher_recents
  @ColorRes
  protected val recentsHeaderColor: Int = R.color.colorPrimary

  private val connection = BackendServiceConnection()
  protected val backend: BehaviorSubject<Optional<Backend>>
    get() = connection.backend

  private var uiModeManager: UiModeManager? = null
  private var nightMode: Int? = null

  protected fun runInBackground(f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager().handlerService.backend(f)
    }
  }

  protected fun runInBackgroundDelayed(delayMillis: Long, f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager().handlerService.backendDelayed(delayMillis, f)
    }
  }

  fun connectToAccount(accountId: Long) {
    getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editCommit {
      putBoolean(Keys.Status.reconnect, false)
    }
    getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE).editCommit {
      putLong(Keys.Status.selectedAccount, accountId)
      putBoolean(Keys.Status.reconnect, true)
    }
  }

  @Inject
  lateinit var connectionSettings: ConnectionSettings

  @Inject
  lateinit var viewModel: QuasselViewModel

  protected var accountId: Long = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.backendWrapper.onNext(this.backend)
    updateRecentsHeader()
    connection.context = this
    lifecycle.addObserver(connection)
    checkConnection()

    if (appearanceSettings.keepScreenOn) {
      window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
  }

  fun updateRecentsHeader() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
      updateRecentsHeaderIfExisting(title.toString(), icon, recentsHeaderColor)
    }
  }

  override fun setTitle(title: CharSequence?) {
    super.setTitle(title)
    updateRecentsHeader()
  }

  override fun onStart() {
    if (Settings.appearance(this) != appearanceSettings ||
      (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) != nightMode) {
      recreate()
    }
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      registerOnSharedPreferenceChangeListener(this@ServiceBoundActivity)
    }
    checkConnection()
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      unregisterOnSharedPreferenceChangeListener(this@ServiceBoundActivity)
    }
  }

  override fun onDestroy() {
    lifecycle.removeObserver(connection)
    super.onDestroy()
  }

  override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) =
    checkConnection()

  protected fun checkConnection() {
    accountId = getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
      ?.getLong(Keys.Status.selectedAccount, -1) ?: -1

    val reconnect = sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      getBoolean(Keys.Status.reconnect, false)
    }
    val accountIdValid = accountId != -1L
    if (!reconnect || !accountIdValid) {
      onSelectAccount()
    } else {
      connection.start()
      connection.bind()
    }
  }

  protected open fun onSelectAccount() = Unit

  companion object {
    const val REQUEST_SELECT_ACCOUNT = 1
  }
}
