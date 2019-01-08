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

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.libquassel.session.SignalProxy
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

abstract class SyncableObject(
  override var proxy: SignalProxy,
  final override val className: String
) : ISyncableObject {
  final override var objectName: String = ""
    private set
  override var identifier = Pair(className, objectName)
  override var initialized: Boolean
    get() = _liveInitialized.value
    set(value) {
      _liveInitialized.onNext(value)
    }

  private val _liveInitialized = BehaviorSubject.createDefault(false)
  override val liveInitialized: Observable<Boolean>
    get() = _liveInitialized

  protected fun renameObject(newName: String) {
    val oldName = objectName
    if (!initialized) {
      objectName = newName
      identifier = Pair(className, objectName)
    } else if (oldName != newName) {
      objectName = newName
      identifier = Pair(className, objectName)
      proxy.renameObject(this, newName, oldName)
    }
  }

  override fun deinit() {
    this.proxy = SignalProxy.NULL
  }

  override fun toString() = "${identifier.first}:${identifier.second}"
}
