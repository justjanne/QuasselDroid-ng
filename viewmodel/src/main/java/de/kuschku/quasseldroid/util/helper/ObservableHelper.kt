/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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
@file:Suppress("NOTHING_TO_INLINE")

package de.kuschku.quasseldroid.util.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import de.kuschku.libquassel.util.compatibility.HandlerService
import io.reactivex.*
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

inline fun <T> Observable<T>.toLiveData(
  strategy: BackpressureStrategy = BackpressureStrategy.LATEST,
  handlerService: HandlerService? = null,
  scheduler: Scheduler = handlerService?.scheduler ?: Schedulers.computation()
): LiveData<T> =
  LiveDataReactiveStreams.fromPublisher(subscribeOn(scheduler).toFlowable(strategy))

inline fun <T> Maybe<T>.toLiveData(
  handlerService: HandlerService? = null,
  scheduler: Scheduler = handlerService?.scheduler ?: Schedulers.computation()
): LiveData<T> =
  LiveDataReactiveStreams.fromPublisher(subscribeOn(scheduler).toFlowable())

inline fun <T> Flowable<T>.toLiveData(
  handlerService: HandlerService? = null,
  scheduler: Scheduler = handlerService?.scheduler ?: Schedulers.computation()
): LiveData<T> = LiveDataReactiveStreams.fromPublisher(subscribeOn(scheduler))

inline fun <reified A, B> combineLatest(
  a: ObservableSource<A>,
  b: ObservableSource<B>
): Observable<Pair<A, B>> =
  Observable.combineLatest(a, b, BiFunction(::Pair))

inline fun <reified A, B, C> combineLatest(
  a: ObservableSource<A>,
  b: ObservableSource<B>,
  c: ObservableSource<C>
): Observable<Triple<A, B, C>> =
  Observable.combineLatest(listOf(a, b, c)) {
    Triple(it[0], it[1], it[2]) as Triple<A, B, C>
  }

inline fun <reified A, B, C, D> combineLatest(
  a: ObservableSource<A>,
  b: ObservableSource<B>,
  c: ObservableSource<C>,
  d: ObservableSource<D>
): Observable<Tuple4<A, B, C, D>> =
  Observable.combineLatest(listOf(a, b, c, d)) {
    Tuple4(it[0], it[1], it[2], it[3]) as Tuple4<A, B, C, D>
  }

inline fun <reified A, B, C, D, E> combineLatest(
  a: ObservableSource<A>,
  b: ObservableSource<B>,
  c: ObservableSource<C>,
  d: ObservableSource<D>,
  e: ObservableSource<E>
): Observable<Tuple5<A, B, C, D, E>> =
  Observable.combineLatest(listOf(a, b, c, d, e)) {
    Tuple5(it[0], it[1], it[2], it[3], it[4]) as Tuple5<A, B, C, D, E>
  }

inline fun <reified A, B, C, D, E, F> combineLatest(
  a: ObservableSource<A>,
  b: ObservableSource<B>,
  c: ObservableSource<C>,
  d: ObservableSource<D>,
  e: ObservableSource<E>,
  f: ObservableSource<F>
): Observable<Tuple6<A, B, C, D, E, F>> =
  Observable.combineLatest(listOf(a, b, c, d, e, f)) {
    Tuple6(it[0], it[1], it[2], it[3], it[4], it[5]) as Tuple6<A, B, C, D, E, F>
  }

inline fun <reified T> combineLatest(sources: Iterable<ObservableSource<out T>?>) =
  Observable.combineLatest(sources) { t -> t.toList() as List<T> }

inline operator fun <T, U> Observable<T>.invoke(f: (T) -> U?) =
  blockingLatest().firstOrNull()?.let(f)

data class Tuple4<out A, out B, out C, out D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
)

data class Tuple5<out A, out B, out C, out D, out E>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E
)

data class Tuple6<out A, out B, out C, out D, out E, out F>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F
)
