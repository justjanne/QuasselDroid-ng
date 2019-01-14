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

package de.kuschku.quasseldroid.ui.setup

import android.content.Context
import android.os.Bundle
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.util.service.BackendServiceConnection
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

abstract class ServiceBoundSlideFragment : SlideFragment() {
  @Inject
  lateinit var viewModel: QuasselViewModel

  private val connection = BackendServiceConnection()
  protected val backend: BehaviorSubject<Optional<Backend>>
    get() = connection.backend

  protected fun runInBackground(f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager()?.handlerService?.backend(f)
    }
  }

  protected fun runInBackgroundDelayed(delayMillis: Long, f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager()?.handlerService?.backendDelayed(delayMillis, f)
    }
  }

  protected var accountId: Long = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    accountId = context?.getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
      ?.getLong(Keys.Status.selectedAccount, -1) ?: -1

    connection.context = context
    lifecycle.addObserver(connection)
    super.onCreate(savedInstanceState)
  }

  override fun onStart() {
    super.onStart()
    accountId = context?.getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
      ?.getLong(Keys.Status.selectedAccount, -1) ?: -1
  }

  override fun onDestroy() {
    lifecycle.removeObserver(connection)
    super.onDestroy()
  }
}
