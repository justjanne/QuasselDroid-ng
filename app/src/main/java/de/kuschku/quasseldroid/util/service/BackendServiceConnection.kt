/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
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

  override fun onServiceDisconnected(component: ComponentName?) {
    when (component) {
      ComponentName(context, QuasselService::class.java) -> {
        backend.onNext(Optional.empty())
      }
    }
  }

  override fun onServiceConnected(component: ComponentName?, binder: IBinder?) {
    when (component) {
      ComponentName(context, QuasselService::class.java) ->
        if (binder is QuasselBinder) {
          backend.onNext(Optional.of(binder.backend))
        }
    }
  }

  fun start(intent: Intent = QuasselService.intent(context!!)) {
    context?.startService(intent)
  }

  fun bind(intent: Intent = QuasselService.intent(context!!), flags: Int = 0) {
    context?.bindService(intent, this, flags)
  }

  fun stop(intent: Intent = QuasselService.intent(context!!)) {
    context?.stopService(intent)
  }

  fun unbind() {
    context?.unbindService(this)
  }
}
