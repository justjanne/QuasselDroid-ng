package de.kuschku.quasseldroid.util.helper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction

inline fun <T> Observable<T>.toLiveData(
  strategy: BackpressureStrategy = BackpressureStrategy.LATEST
): LiveData<T> = LiveDataReactiveStreams.fromPublisher(toFlowable(strategy))

inline fun <reified A, B> combineLatest(
  a: ObservableSource<A>,
  b: ObservableSource<B>
): Observable<Pair<A, B>> = Observable.combineLatest(a, b, BiFunction(::Pair))

inline fun <reified A, B, C> combineLatest(
  a: ObservableSource<A>,
  b: ObservableSource<B>,
  c: ObservableSource<C>
): Observable<Triple<A, B, C>> = Observable.combineLatest(listOf(a, b, c), { (t0, t1, t2) ->
  Triple(t0, t1, t2) as Triple<A, B, C>
})

inline fun <reified T> combineLatest(sources: Iterable<ObservableSource<out T>?>) =
  Observable.combineLatest(sources) { t -> t.toList() as List<T> }

inline operator fun <T, U> Observable<T>.invoke(f: (T) -> U?) =
  blockingLatest().firstOrNull()?.let(f)