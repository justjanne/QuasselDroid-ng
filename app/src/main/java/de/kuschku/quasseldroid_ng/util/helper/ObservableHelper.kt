package de.kuschku.quasseldroid_ng.util.helper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

inline fun <T> Observable<T>.toLiveData(
  strategy: BackpressureStrategy = BackpressureStrategy.LATEST
): LiveData<T> = LiveDataReactiveStreams.fromPublisher(toFlowable(strategy))
