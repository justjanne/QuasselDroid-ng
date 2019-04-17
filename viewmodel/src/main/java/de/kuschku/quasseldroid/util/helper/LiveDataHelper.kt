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

import androidx.annotation.MainThread
import androidx.lifecycle.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

@MainThread
inline fun <X, Y> LiveData<X?>.switchMap(
  crossinline func: (X) -> LiveData<Y>?
): LiveData<Y> {
  val result = MediatorLiveData<Y>()
  result.addSource(
    this, object : Observer<X?> {
    var mSource: LiveData<Y>? = null

    override fun onChanged(x: X?) {
      val newLiveData = if (x == null) null else func(x)
      if (mSource === newLiveData) {
        return
      }
      mSource?.let(result::removeSource)
      mSource = newLiveData
      if (newLiveData != null) {
        result.addSource(newLiveData) { y -> result.value = y }
      } else {
        result.value = null
      }
    }
  })
  return result
}

@MainThread
inline fun <X, Y> LiveData<X>.switchMapNotNull(
  crossinline func: (X) -> LiveData<Y>?
): LiveData<Y> {
  val result = MediatorLiveData<Y>()
  result.addSource(
    this, object : Observer<X> {
    var mSource: LiveData<Y>? = null

    override fun onChanged(x: X?) {
      val newLiveData = if (x == null) null else func(x)
      if (mSource === newLiveData) {
        return
      }
      mSource?.let(result::removeSource)
      mSource = newLiveData
      if (newLiveData != null) {
        result.addSource(newLiveData) { y -> result.value = y }
      } else {
        result.value = null
      }
    }
  })
  return result
}

@MainThread
inline fun <X, Y> LiveData<X?>.switchMapRx(
  strategy: BackpressureStrategy,
  crossinline func: (X) -> Observable<Y>?
): LiveData<Y?> {
  val result = MediatorLiveData<Y>()
  result.addSource(
    this, object : Observer<X?> {
    var mSource: LiveData<Y>? = null

    override fun onChanged(x: X?) {
      val newLiveData = if (x == null) null else func(x)?.toLiveData(strategy)
      if (mSource === newLiveData) {
        return
      }
      mSource?.let(result::removeSource)
      mSource = newLiveData
      if (newLiveData != null) {
        result.addSource(newLiveData) { y -> result.value = y }
      } else {
        result.value = null
      }
    }
  })
  return result
}

@MainThread
inline fun <X, Y> LiveData<out X?>.map(
  crossinline func: (X) -> Y?
): LiveData<Y?> {
  val result = MediatorLiveData<Y?>()
  result.addSource(this) { x ->
    result.value = if (x == null) null else func.invoke(x)
  }
  return result
}

@MainThread
inline fun <X> LiveData<X?>.or(
  default: X
): LiveData<X> {
  val result = object : MediatorLiveData<X>() {
    override fun getValue() = super.getValue() ?: default
  }
  result.addSource(this) { x ->
    result.value = x ?: default
  }
  return result
}

inline fun <T> LiveData<T>.observeSticky(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
  observe(lifecycleOwner, observer)
  observer.onChanged(value)
}

inline fun <T> LiveData<T>.observeForeverSticky(observer: Observer<T>) {
  observeForever(observer)
  observer.onChanged(value)
}

inline fun <T> LiveData<T>.toObservable(lifecycleOwner: LifecycleOwner): Observable<T> =
  Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, this))


inline operator fun <T> LiveData<T>.invoke() = value

inline operator fun <T, U> LiveData<T?>.invoke(f: (T) -> U?) = value?.let(f)

inline fun <T, U> LiveData<T>.let(f: (T) -> U?) = value?.let(f)
