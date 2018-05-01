/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.util.helpers

import de.kuschku.libquassel.util.Optional
import io.reactivex.Observable

fun <T> Observable<T>.or(default: T): T = try {
  this.blockingLatest().firstOrNull() ?: default
} catch (_: Throwable) {
  default
}

val <T : Any> Observable<T>.value
  get() = this.map { Optional.of(it) }.blockingMostRecent(Optional.empty()).firstOrNull()?.orNull()

fun <T : Any, U : Any> Observable<Optional<T>>.mapMap(mapper: (T) -> U): Observable<Optional<U>> =
  map { it.map(mapper) }

fun <T : Any, U : Any> Observable<Optional<T>>.mapMapNullable(
  mapper: (T) -> U?): Observable<Optional<U>> = map {
  it.flatMap {
    Optional.ofNullable(mapper(it))
  }
}

fun <T : Any, U : Any> Observable<Optional<T>>.mapSwitchMap(
  mapper: (T) -> Observable<U>): Observable<Optional<U>> = switchMap {
  if (it.isPresent()) {
    it.map(mapper).get().map { Optional.of(it) }
  } else {
    Observable.just(Optional.empty())
  }
}

fun <T : Any, U : Any> Observable<Optional<T>>.mapSwitchMapEmpty(
  mapper: (T) -> Observable<U>): Observable<U> = switchMap {
  if (it.isPresent()) {
    it.map(mapper).get()
  } else {
    Observable.empty()
  }
}

fun <T : Any, U : Any> Observable<Optional<T>>.flatMapSwitchMap(
  mapper: (T) -> Observable<Optional<U>>): Observable<Optional<U>> = switchMap {
  it.map(mapper).orElse(Observable.just(Optional.empty()))
}

fun <T : Any> Observable<Optional<T>>.mapOrElse(orElse: T): Observable<T> = map {
  it.orElse(orElse)
}
