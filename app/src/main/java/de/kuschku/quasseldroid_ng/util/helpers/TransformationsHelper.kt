package de.kuschku.quasseldroid_ng.util.helpers

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.support.annotation.MainThread

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
