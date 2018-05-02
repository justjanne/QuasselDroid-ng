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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.service.QuasselBinder
import de.kuschku.quasseldroid.service.QuasselService
import io.reactivex.subjects.BehaviorSubject

class BackendServiceConnection : ServiceConnection {
  val backend: BehaviorSubject<Optional<Backend>> = BehaviorSubject.createDefault(Optional.empty())

  var context: Context? = null

  private var bound: Boolean = false

  override fun onServiceDisconnected(component: ComponentName?) {
    when (component) {
      ComponentName(context, QuasselService::class.java) -> {
        bound = false
        backend.onNext(Optional.empty())
      }
    }
  }

  override fun onServiceConnected(component: ComponentName?, binder: IBinder?) {
    when (component) {
      ComponentName(context, QuasselService::class.java) ->
        if (binder is QuasselBinder) {
          bound = true
          backend.onNext(Optional.of(binder.backend))
        }
    }
  }

  fun start(intent: Intent = QuasselService.intent(context!!)) {
    context?.startService(intent)
  }

  @Synchronized
  fun bind(intent: Intent = QuasselService.intent(context!!), flags: Int = 0) {
    context?.bindService(intent, this, flags)
  }

  fun stop(intent: Intent = QuasselService.intent(context!!)) {
    context?.stopService(intent)
  }

  @Synchronized
  fun unbind() {
    if (bound) context?.unbindService(this)
  }
}
