package de.kuschku.libquassel.session

import de.kuschku.libquassel.protocol.message.SignalProxyMessage
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class HeartBeatThread(private val session: Session) : Thread() {
  private var running = true
  private var lastHeartBeatReply: Instant = Instant.now()
  override fun run() {
    while (running) {
      Thread.sleep(30_000)
      val now = Instant.now()
      if (Duration.between(lastHeartBeatReply, now).toMillis() > TIMEOUT) {
        session.close()
      } else {
        session.dispatch(SignalProxyMessage.HeartBeat(now))
      }
    }
  }

  fun end() {
    running = false
  }

  fun setLastHeartBeatReply(time: Instant) {
    this.lastHeartBeatReply = time
  }

  companion object {
    // Timeout, set to 2 minutes
    const val TIMEOUT = 120_000L
  }
}
