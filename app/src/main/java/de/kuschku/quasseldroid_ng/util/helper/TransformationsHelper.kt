package de.kuschku.quasseldroid_ng.util.helper

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

@MainThread
fun <X, Y> stickySwitchMapNotNull(
  trigger: LiveData<X?>,
  func: (X) -> LiveData<Y>?,
  defaultValue: Y
): LiveData<Y> {
  val result = object : MediatorLiveData<Y>() {
    override fun observe(owner: LifecycleOwner?, observer: Observer<Y>?) {
      super.observe(owner, observer)
      observer?.onChanged(value ?: defaultValue)
    }

    override fun observeForever(observer: Observer<Y>?) {
      super.observeForever(observer)
      observer?.onChanged(value ?: defaultValue)
    }
  }
  result.addSource(trigger, object : Observer<X?> {
    internal var mSource: LiveData<Y>? = null

    override fun onChanged(x: X?) {
      val newLiveData = if (x != null) func(x) else null
      if (mSource === newLiveData) {
        return
      }
      if (mSource != null) {
        result.removeSource(mSource)
      }
      mSource = newLiveData
      if (mSource != null) {
        result.addSource(mSource) { y -> result.value = y ?: defaultValue }
      } else {
        result.value = defaultValue
      }
    }
  })
  return result
}

@MainThread
inline fun <X, Y> rxStickySwitchMapNotNull(
  trigger: LiveData<X?>,
  crossinline func: (X) -> BehaviorSubject<Y>?,
  defaultValue: Y
): LiveData<Y> {
  return stickySwitchMapNotNull(trigger, {
    val data = func(it)
    if (data != null)
      BehaviorSubjectLiveData(data)
    else
      null
  }, defaultValue)
}

class BehaviorSubjectLiveData<T>(val observable: BehaviorSubject<T>) : LiveData<T>() {
  var subscription: Disposable? = null

  override fun getValue(): T? {
    return observable.value
  }

  override fun setValue(value: T) {
    observable.onNext(value)
  }

  override fun postValue(value: T) {
    observable.onNext(value)
  }

  override fun observe(owner: LifecycleOwner?, observer: Observer<T>?) {
    super.observe(owner, observer)
    if (subscription == null && hasActiveObservers()) {
      subscription = observable.subscribeOn(Schedulers.io()).subscribe(this::postValue)
    }
  }

  override fun observeForever(observer: Observer<T>?) {
    super.observeForever(observer)
    if (subscription == null && hasActiveObservers()) {
      subscription = observable.subscribeOn(Schedulers.io()).subscribe(this::postValue)
    }
  }

  override fun removeObserver(observer: Observer<T>?) {
    super.removeObserver(observer)
    if (subscription != null && !hasActiveObservers()) {
      subscription?.dispose()
    }
  }

  override fun removeObservers(owner: LifecycleOwner?) {
    super.removeObservers(owner)
    if (subscription != null && !hasActiveObservers()) {
      subscription?.dispose()
    }
  }
}

@MainThread
fun <X, Y> rxSwitchMap(
  trigger: LiveData<X?>,
  func: (X) -> Observable<Y>?,
  defaultValue: Y
): LiveData<Y> {
  val result = object : MediatorLiveData<Y>() {
    override fun observe(owner: LifecycleOwner?, observer: Observer<Y>?) {
      super.observe(owner, observer)
      observer?.onChanged(value ?: defaultValue)
    }

    override fun observeForever(observer: Observer<Y>?) {
      super.observeForever(observer)
      observer?.onChanged(value ?: defaultValue)
    }
  }
  result.addSource(trigger, object : Observer<X?> {
    internal var mSource: Observable<Y>? = null

    override fun onChanged(x: X?) {
      val newLiveData = if (x != null) func(x) else null

    }
  })
  return result
}

@MainThread
fun <X, Y> stickyMapNotNull(
  trigger: LiveData<X?>,
  func: (X) -> Y?,
  defaultValue: Y
): LiveData<Y> {
  val result = object : MediatorLiveData<Y>() {
    override fun observe(owner: LifecycleOwner?, observer: Observer<Y>?) {
      super.observe(owner, observer)
      observer?.onChanged(value ?: defaultValue)
    }

    override fun observeForever(observer: Observer<Y>?) {
      super.observeForever(observer)
      observer?.onChanged(value ?: defaultValue)
    }
  }
  result.addSource(trigger) { x ->
    result.setValue(if (x == null) defaultValue else func(x) ?: defaultValue)
  }
  return result
}
