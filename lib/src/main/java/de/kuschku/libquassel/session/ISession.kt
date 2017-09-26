package de.kuschku.libquassel.session

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import java.io.Closeable

interface ISession : Closeable {
  val state: Flowable<ConnectionState>

  companion object {
    val NULL = object : ISession {
      override fun close() = Unit
      override val state: Flowable<ConnectionState>
        = BehaviorSubject.createDefault(ConnectionState.DISCONNECTED)
        .toFlowable(BackpressureStrategy.LATEST)
    }
  }
}
