package de.kuschku.quasseldroid.util.helper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.ObservableSource

inline fun <T> Observable<T>.toLiveData(
  strategy: BackpressureStrategy = BackpressureStrategy.LATEST
): LiveData<T> = LiveDataReactiveStreams.fromPublisher(toFlowable(strategy))

inline fun <reified T> combineLatest(sources: Iterable<ObservableSource<out T>?>) =
  Observable.combineLatest(sources) { t -> t.toList() as List<T> }