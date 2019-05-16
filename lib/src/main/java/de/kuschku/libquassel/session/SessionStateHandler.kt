/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.libquassel.session

import de.kuschku.libquassel.session.manager.SessionState
import de.kuschku.libquassel.util.helper.safeSwitchMap
import io.reactivex.subjects.BehaviorSubject

open class SessionStateHandler constructor(
  initialState: SessionState
) {
  private val sessions = BehaviorSubject.createDefault(initialState)

  val connectedSession = sessions.map {
    it.connected
    ?: it.offline
  }

  val connectingSession = sessions.map {
    it.connecting
    ?: it.connected
    ?: it.offline
  }

  val progressData = connectingSession.map {
    it.progress
  }

  val errors = progressData.safeSwitchMap {
    it.error
  }

  val progress = progressData.safeSwitchMap {
    it.progress
  }

  val state = progressData.safeSwitchMap {
    it.state
  }

  private fun updateState(f: SessionState.() -> SessionState) {
    sessions.onNext(f(sessions.value))
  }

  protected fun updateStateConnecting(connectingSession: ISession) = updateState {
    connecting?.close()
    copy(connecting = connectingSession)
  }

  protected fun updateStateConnected() = updateState {
    connected?.close()
    copy(connected = connecting, connecting = null)
  }

  protected fun updateStateOffline() = updateState {
    connected?.close()
    connecting?.close()
    copy(connecting = null, connected = null)
  }
}
