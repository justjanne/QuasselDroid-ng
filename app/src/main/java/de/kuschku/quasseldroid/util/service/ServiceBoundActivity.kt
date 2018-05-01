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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.ConnectionSettings
import de.kuschku.quasseldroid.settings.Settings
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity
import de.kuschku.quasseldroid.util.helper.sharedPreferences
import de.kuschku.quasseldroid.util.helper.updateRecentsHeaderIfExisting
import de.kuschku.quasseldroid.util.ui.ThemedActivity
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

abstract class ServiceBoundActivity : ThemedActivity(),
                                      SharedPreferences.OnSharedPreferenceChangeListener {
  @DrawableRes
  protected val icon: Int = R.mipmap.ic_launcher_recents
  @ColorRes
  protected val recentsHeaderColor: Int = R.color.colorPrimary

  private val connection = BackendServiceConnection()
  protected val backend: BehaviorSubject<Optional<Backend>>
    get() = connection.backend

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

  @Inject
  lateinit var autoCompleteSettings: AutoCompleteSettings

  @Inject
  lateinit var connectionSettings: ConnectionSettings

  @Inject
  lateinit var viewModel: QuasselViewModel

  protected var accountId: Long = -1

  private var startedSelection = false

  override fun onCreate(savedInstanceState: Bundle?) {
    connection.context = this
    checkConnection()
    super.onCreate(savedInstanceState)
    viewModel.backendWrapper.onNext(this.backend)
    updateRecentsHeader()
  }

  fun updateRecentsHeader() =
    updateRecentsHeaderIfExisting(title.toString(), icon, recentsHeaderColor)

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

    val reconnect = sharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE) {
      getBoolean(Keys.Status.reconnect, false)
    }
    val accountIdValid = accountId != -1L

    if (!reconnect || !accountIdValid) {

      if (!startedSelection) {
        startActivityForResult(AccountSelectionActivity.intent(this), REQUEST_SELECT_ACCOUNT)
        startedSelection = true
      }
    } else {
      connection.start()
      connection.bind()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == REQUEST_SELECT_ACCOUNT) {
      startedSelection = false

      if (resultCode == Activity.RESULT_CANCELED) {
        finish()
      } else {
        checkConnection()
      }
    }
  }

  protected fun stopService() {
    connection.unbind()
    connection.stop()
  }

  companion object {
    const val REQUEST_SELECT_ACCOUNT = 1
  }
}
