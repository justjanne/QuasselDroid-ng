package de.kuschku.libquassel.util.helpers

import android.arch.lifecycle.LiveDataReactiveStreams
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

inline fun <T> Observable<T>.toLiveData(
  strategy: BackpressureStrategy = BackpressureStrategy.LATEST)
  = LiveDataReactiveStreams.fromPublisher(toFlowable(strategy))
