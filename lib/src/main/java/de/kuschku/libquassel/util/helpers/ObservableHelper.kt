package de.kuschku.libquassel.util.helpers

import io.reactivex.Observable

fun <T> Observable<T>.or(default: T): T
  = this.blockingLatest().firstOrNull() ?: default
