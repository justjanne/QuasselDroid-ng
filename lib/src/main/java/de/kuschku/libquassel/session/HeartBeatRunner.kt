package de.kuschku.libquassel.session

import org.threeten.bp.Instant

interface HeartBeatRunner {
  fun start()
  fun end()

  fun setLastHeartBeatReply(time: Instant)

  companion object {
    const val TIMEOUT = 120_000L
    const val DELAY = 30_000L
  }
}
