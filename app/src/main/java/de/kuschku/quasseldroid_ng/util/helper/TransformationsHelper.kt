package de.kuschku.quasseldroid_ng.util.helper

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread

@MainThread
fun <X, Y> LiveData<X?>.stickySwitchMapNotNull(
  defaultValue: Y,
  func: (X) -> LiveData<Y>?
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
  result.addSource(this, object : Observer<X?> {
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
fun <X, Y> LiveData<X?>.switchMapNullable(
  defaultValue: Y,
  func: (X) -> LiveData<Y>?
): LiveData<Y> {
  val result = MediatorLiveData<Y>()
  result.addSource(this, object : Observer<X?> {
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
fun <X, Y> LiveData<X?>.stickyMapNotNull(
  defaultValue: Y,
  func: (X) -> Y?
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
  result.addSource(this) { x ->
    result.setValue(if (x == null) defaultValue else func(x) ?: defaultValue)
  }
  return result
}
