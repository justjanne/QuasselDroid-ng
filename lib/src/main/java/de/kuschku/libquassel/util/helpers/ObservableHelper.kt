package de.kuschku.libquassel.util.helpers

import io.reactivex.Observable

fun <T> Observable<T>.or(default: T): T = try {
  this.blockingLatest().firstOrNull() ?: default
} catch (_: Throwable) {
  default
}

val <T> Observable<T>.value
  get() = this.blockingLatest().firstOrNull()