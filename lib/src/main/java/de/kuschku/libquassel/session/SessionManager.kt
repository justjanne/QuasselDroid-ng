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

package de.kuschku.libquassel.session

import de.kuschku.libquassel.connection.ConnectionState
import de.kuschku.libquassel.connection.HostnameVerifier
import de.kuschku.libquassel.connection.SocketAddress
import de.kuschku.libquassel.protocol.ClientData
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.DEBUG
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel.INFO
import de.kuschku.libquassel.util.helpers.or
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import javax.net.ssl.X509TrustManager

class SessionManager(
  offlineSession: ISession,
  private val backlogStorage: BacklogStorage,
  private val notificationManager: NotificationManager?,
  val handlerService: HandlerService,
  private val heartBeatFactory: (Session) -> HeartBeatRunner,
  private val disconnectFromCore: () -> Unit,
  private val initCallback: (Session) -> Unit,
  private val exceptionHandler: (Throwable) -> Unit
) {
  fun close() = session.or(lastSession).close()

  private var lastClientData: ClientData? = null
  private var lastTrustManager: X509TrustManager? = null
  private var lastHostnameVerifier: HostnameVerifier? = null
  private var lastAddress: SocketAddress? = null
  private var lastUserData: Pair<String, String>? = null
  private var lastShouldReconnect = false

  private var inProgressSession = BehaviorSubject.createDefault(ISession.NULL)
  private var lastSession: ISession = offlineSession
  val state: Observable<ConnectionState> = inProgressSession.switchMap(ISession::state)

  private val initStatus: Observable<Pair<Int, Int>> = inProgressSession.switchMap(ISession::initStatus)
  val session: Observable<ISession> = state.map { connectionState ->
    if (connectionState == ConnectionState.CONNECTED)
      inProgressSession.value
    else
      lastSession
  }

  private var hasErrored: Boolean = false

  val error: Flowable<Error>
    get() = inProgressSession
      .toFlowable(BackpressureStrategy.LATEST)
      .switchMap(ISession::error)

  val connectionProgress: Observable<Triple<ConnectionState, Int, Int>> = Observable.combineLatest(
    state, initStatus,
    BiFunction<ConnectionState, Pair<Int, Int>, Triple<ConnectionState, Int, Int>> { t1, t2 ->
      Triple(t1, t2.first, t2.second)
    })

  init {
    log(INFO, "Session", "Session created")

    state.subscribe {
      if (it == ConnectionState.CONNECTED) {
        lastSession.close()
      }
    }

    error.subscribe {
      hasErrored = true
    }

    // This should preload them
    Invokers
  }

  fun ifDisconnected(closure: (ISession) -> Unit) {
    state.or(ConnectionState.DISCONNECTED).let {
      if (it == ConnectionState.CLOSED || it == ConnectionState.DISCONNECTED) {
        closure(inProgressSession.value)
      }
    }
  }

  fun connect(
    clientData: ClientData,
    trustManager: X509TrustManager,
    hostnameVerifier: HostnameVerifier,
    address: SocketAddress,
    userData: Pair<String, String>,
    shouldReconnect: Boolean = false
  ) {
    log(DEBUG, "SessionManager", "Connecting")
    inProgressSession.value.close()
    lastClientData = clientData
    lastTrustManager = trustManager
    lastHostnameVerifier = hostnameVerifier
    lastAddress = address
    lastUserData = userData
    lastShouldReconnect = shouldReconnect
    hasErrored = false
    inProgressSession.onNext(
      Session(
        clientData,
        trustManager,
        hostnameVerifier,
        address,
        handlerService,
        backlogStorage,
        notificationManager,
        userData,
        heartBeatFactory,
        disconnectFromCore,
        initCallback,
        exceptionHandler
      )
    )
  }

  /**
   * @return if an autoreconnect has been necessary
   */
  fun autoReconnect(forceReconnect: Boolean = false) = if (!hasErrored) {
    state.or(ConnectionState.DISCONNECTED).let {
      if (it == ConnectionState.CLOSED) {
        log(INFO, "SessionManager", "Autoreconnect triggered")
        reconnect(forceReconnect)
        true
      } else {
        log(INFO, "SessionManager", "Autoreconnect failed: state is $it")
        false
      }
    }
  } else {
    log(INFO, "SessionManager", "Autoreconnect failed: hasErrored")
    false
  }

  fun reconnect(forceReconnect: Boolean = false) {
    if (lastShouldReconnect || forceReconnect) {
      val clientData = lastClientData
      val trustManager = lastTrustManager
      val hostnameVerifier = lastHostnameVerifier
      val address = lastAddress
      val userData = lastUserData

      if (clientData != null && trustManager != null && hostnameVerifier != null && address != null && userData != null) {
        connect(clientData, trustManager, hostnameVerifier, address, userData, forceReconnect)
      } else {
        log(INFO, "SessionManager", "Reconnect failed: not enough data available")
      }
    } else {
      log(INFO, "SessionManager", "Reconnect failed: reconnect not allowed")
    }
  }

  fun disconnect(forever: Boolean) {
    if (forever)
      backlogStorage.clearMessages()
    inProgressSession.value.close()
    inProgressSession.onNext(ISession.NULL)
  }

  fun login(user: String, pass: String) {
    inProgressSession.value.login(user, pass)
  }
}
