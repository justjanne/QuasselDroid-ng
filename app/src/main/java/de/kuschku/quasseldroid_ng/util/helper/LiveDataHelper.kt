package de.kuschku.quasseldroid_ng.util.helper

import android.arch.lifecycle.*
import android.support.annotation.MainThread
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

@MainThread
inline fun <X, Y> LiveData<X?>.switchMap(
  noinline func: (X) -> LiveData<Y>?
): LiveData<Y> {
  val result = MediatorLiveData<Y>()
  result.addSource(this, object : Observer<X?> {
    internal var mSource: LiveData<Y>? = null

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
  noinline func: (X) -> Observable<Y>?
): LiveData<Y?> {
  val result = MediatorLiveData<Y>()
  result.addSource(this, object : Observer<X?> {
    internal var mSource: LiveData<Y>? = null

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
inline fun <X, Y> LiveData<X?>.switchMapRx(
  noinline func: (X) -> Observable<Y>?
): LiveData<Y?> = switchMapRx(BackpressureStrategy.LATEST, func)

@MainThread
inline fun <X, Y> LiveData<X?>.map(
  noinline func: (X) -> Y?
): LiveData<Y?> {
  val result = MediatorLiveData<Y?>()
  result.addSource(this) { x ->
    result.value = if (x == null) null else func.invoke(x)
  }
  return result
}

@MainThread
inline fun <X> LiveData<X>.orElse(
  noinline func: () -> X
): LiveData<X> {
  val result = object : MediatorLiveData<X>() {
    override fun getValue() = super.getValue() ?: func()
  }
  result.addSource(this) { x ->
    result.value = x ?: func()
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

inline fun <T> LiveData<T>.toObservable(lifecycleOwner: LifecycleOwner): Observable<T>
  = Observable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, this))
