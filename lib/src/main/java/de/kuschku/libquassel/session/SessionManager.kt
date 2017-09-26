package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.ClientData
import de.kuschku.libquassel.quassel.syncables.interfaces.invokers.Invokers
import de.kuschku.libquassel.util.compatibility.HandlerService
import de.kuschku.libquassel.util.compatibility.LoggingHandler
import de.kuschku.libquassel.util.compatibility.log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import javax.net.ssl.X509TrustManager

class SessionManager(
  private val offlineSession: ISession
) {
  init {
    log(LoggingHandler.LogLevel.INFO, "Session", "Session created")

    // This should preload them
    Invokers
  }

  fun connect(
    clientData: ClientData,
    trustManager: X509TrustManager,
    address: SocketAddress,
    handlerService: HandlerService,
    userData: Pair<String, String>
  ) {
    inProgressSession.value.close()
    inProgressSession.onNext(Session(clientData, trustManager, address, handlerService, userData))
  }

  fun disconnect() {
    inProgressSession.value.close()
    inProgressSession.onNext(offlineSession)
  }

  private var inProgressSession = BehaviorSubject.createDefault(offlineSession)
  private val inProgressSessionPublisher: Flowable<ISession>
    = inProgressSession.toFlowable(BackpressureStrategy.LATEST)
  val state = inProgressSessionPublisher.switchMap { it.state }
  val session = state.map { connectionState ->
    if (connectionState == ConnectionState.CONNECTED)
      inProgressSession.value
    else
      offlineSession
  }
}
