/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.libquassel.session.manager.ConnectionInfo
import de.kuschku.libquassel.session.manager.SessionState
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.*
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.or
import de.kuschku.libquassel.util.helper.safeSwitchMap
import de.kuschku.libquassel.util.helper.value
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class SessionManager(
  offlineSession: ISession,
  private val backlogStorage: BacklogStorage,
  private val notificationManager: NotificationManager?,
  val handlerService: HandlerService,
  private val heartBeatFactory: () -> HeartBeatRunner,
  private val exceptionHandler: (Throwable) -> Unit
) : SessionStateHandler(SessionState(offlineSession, null, null)) {
  // Stateful fields
  private var lastConnectionInfo: ConnectionInfo? = null
  private var hasErrored: Boolean = false
  private var hasBeenDisconnected: Boolean = false

  private val disposables = mutableListOf<Disposable>()

  // Helping Rx Mappers
  val connectionProgress: Observable<Triple<ConnectionState, Int, Int>> = progressData.safeSwitchMap {
    combineLatest(it.state, it.progress).map { (state, progress) ->
      Triple(state, progress.first, progress.second)
    }
  }

  // Listeners
  private var disconnectFromCore: (() -> Unit)? = null
  private var initCallback: ((Session) -> Unit)? = null

  fun setDisconnectFromCore(callback: (() -> Unit)?) {
    this.disconnectFromCore = callback
  }

  fun setInitCallback(callback: ((Session) -> Unit)?) {
    this.initCallback = callback
  }

  init {
    log(INFO, "Session", "Session created")

    disposables.add(state.distinctUntilChanged().subscribe { state: ConnectionState ->
      if (state == ConnectionState.CONNECTED) {
        updateStateConnected()
      }
    })

    // This should preload them
    Invokers
  }

  fun login(user: String, pass: String) {
    connectingSession.value?.login(user, pass)
  }

  fun setupCore(setupData: HandshakeMessage.CoreSetupData) {
    connectingSession.value?.setupCore(setupData)
  }

  fun connect(
    connectionInfo: ConnectionInfo
  ) {
    log(DEBUG, "SessionManager", "Connecting")
    lastConnectionInfo = connectionInfo
    hasErrored = false
    updateStateConnecting(Session(
      connectionInfo.address,
      connectionInfo.userData,
      connectionInfo.requireSsl,
      connectionInfo.trustManager,
      connectionInfo.hostnameVerifier,
      connectionInfo.clientData,
      handlerService,
      heartBeatFactory,
      disconnectFromCore,
      initCallback,
      exceptionHandler,
      ::hasErroredCallback,
      notificationManager,
      backlogStorage
    ))
  }

  fun hasErroredCallback(error: Error) {
    log(WARN, "SessionManager", "Callback Error occured: $error")
    hasErrored = true
  }

  fun canAutoReconnect(
    ignoreConnectionState: Boolean = false,
    ignoreSetting: Boolean = false,
    ignoreErrors: Boolean = false,
    connectionInfo: ConnectionInfo? = lastConnectionInfo
  ): Boolean {
    if (hasBeenDisconnected) {
      log(DEBUG, "SessionManager", "Reconnect not possible: manually disconnected")
      return false
    }

    if (connectionInfo == null) {
      log(DEBUG, "SessionManager", "Reconnect not possible: not enough data available")
      return false
    }

    if (!connectionInfo.shouldReconnect && !ignoreSetting) {
      log(DEBUG, "SessionManager", "Reconnect not possible: reconnect not allowed")
      return false
    }

    val connectionState = state.or(ConnectionState.DISCONNECTED)
    if (connectionState != ConnectionState.DISCONNECTED &&
        connectionState != ConnectionState.CLOSED &&
        !ignoreConnectionState) {
      log(DEBUG, "SessionManager", "Reconnect not possible: connection state is $connectionState")
      return false
    }

    if (hasErrored && !ignoreErrors) {
      log(DEBUG, "SessionManager", "Reconnect not possible: errors have been thrown")
      return false
    }

    return true
  }

  fun autoConnect(
    ignoreConnectionState: Boolean = false,
    ignoreSetting: Boolean = false,
    ignoreErrors: Boolean = false,
    connectionInfo: ConnectionInfo? = lastConnectionInfo
  ): Boolean {
    if (!canAutoReconnect(ignoreConnectionState, ignoreSetting, ignoreErrors, connectionInfo))
      return false

    log(DEBUG, "SessionManager", "Reconnect successful")
    connect(connectionInfo!!)
    return true
  }

  fun disconnect(forever: Boolean) {
    hasBeenDisconnected = true
    if (forever) backlogStorage.clearMessages()
    updateStateOffline()
  }

  fun dispose() {
    setDisconnectFromCore(null)
    setInitCallback(null)
    for (disposable in disposables) {
      if (!disposable.isDisposed)
        disposable.dispose()
    }
  }
}
