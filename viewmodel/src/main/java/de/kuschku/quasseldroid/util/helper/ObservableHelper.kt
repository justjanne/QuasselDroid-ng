package de.kuschku.quasseldroid.util.helper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
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
): Observable<Pair<A, B>> = Observable.combineLatest(a, b, BiFunction(::Pair))

inline fun <reified A, B, C> combineLatest(
  a: ObservableSource<A>,
  b: ObservableSource<B>,
  c: ObservableSource<C>
): Observable<Triple<A, B, C>> = Observable.combineLatest(
  listOf(a, b, c),
  { (t0, t1, t2) ->
    Triple(t0, t1, t2) as Triple<A, B, C>
  }
)

inline fun <reified A, B, C, D> combineLatest(
  a: ObservableSource<A>,
  b: ObservableSource<B>,
  c: ObservableSource<C>,
  d: ObservableSource<D>
): Observable<Tuple4<A, B, C, D>> = Observable.combineLatest(
  listOf(a, b, c, d),
  { (t0, t1, t2, t3) ->
    Tuple4(t0,
           t1,
           t2,
           t3) as Tuple4<A, B, C, D>
  }
)

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
