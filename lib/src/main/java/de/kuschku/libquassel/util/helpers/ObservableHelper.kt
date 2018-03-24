package de.kuschku.libquassel.util.helpers

import de.kuschku.libquassel.util.Optional
import io.reactivex.Observable

fun <T> Observable<T>.or(default: T): T = try {
  this.blockingLatest().firstOrNull() ?: default
} catch (_: Throwable) {
  default
}

val <T> Observable<T>.value
  get() = this.map { Optional.of(it) }.blockingMostRecent(Optional.empty()).firstOrNull()?.orNull()

fun <T, U> Observable<Optional<T>>.mapMap(mapper: (T) -> U): Observable<Optional<U>> = map {
  it.map(mapper)
}

fun <T, U> Observable<Optional<T>>.mapMapNullable(
  mapper: (T) -> U?): Observable<Optional<U>> = map {
  it.flatMap {
    Optional.ofNullable(mapper(it))
  }
}

fun <T, U> Observable<Optional<T>>.mapSwitchMap(
  mapper: (T) -> Observable<U>): Observable<Optional<U>> = switchMap {
  if (it.isPresent()) {
    it.map(mapper).get().map { Optional.of(it) }
  } else {
    Observable.just(Optional.empty())
  }
}

fun <T, U> Observable<Optional<T>>.mapSwitchMapEmpty(
  mapper: (T) -> Observable<U>): Observable<U> = switchMap {
  if (it.isPresent()) {
    it.map(mapper).get()
  } else {
    Observable.empty()
  }
}

fun <T, U> Observable<Optional<T>>.flatMapSwitchMap(
  mapper: (T) -> Observable<Optional<U>>): Observable<Optional<U>> = switchMap {
  it.map(mapper).orElse(Observable.just(Optional.empty()))
}

fun <T> Observable<Optional<T>>.mapOrElse(orElse: T): Observable<T> = map {
  it.orElse(orElse)
}