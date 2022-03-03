package de.justjanne.quasseldroid.util.lifecycle

import android.content.Context
import androidx.annotation.CallSuper
import java.util.concurrent.atomic.AtomicReference

abstract class DefaultContextualLifecycleObserver : ContextualLifecycleObserver {
  private var statusInternal = AtomicReference(LifecycleStatus.DESTROYED)
  protected val status: LifecycleStatus
    get() = statusInternal.get()

  @CallSuper
  override fun onCreate(owner: Context) {
    require(statusInternal.compareAndSet(LifecycleStatus.DESTROYED, LifecycleStatus.CREATED)) {
      "Unexpected lifecycle status: onCreate called, but status is not DESTROYED: ${statusInternal.get()}"
    }
  }

  @CallSuper
  override fun onStart(owner: Context) {
    require(statusInternal.compareAndSet(LifecycleStatus.CREATED, LifecycleStatus.STARTED)) {
      "Unexpected lifecycle status: onStart called, but status is not CREATED: ${statusInternal.get()}"
    }
  }

  @CallSuper
  override fun onResume(owner: Context) {
    require(statusInternal.compareAndSet(LifecycleStatus.STARTED, LifecycleStatus.RESUMED)) {
      "Unexpected lifecycle status: onResume called, but status is not STARTED: ${statusInternal.get()}"
    }
  }

  @CallSuper
  override fun onPause(owner: Context) {
    require(statusInternal.compareAndSet(LifecycleStatus.RESUMED, LifecycleStatus.STARTED)) {
      "Unexpected lifecycle status: onPause called, but status is not RESUMED: ${statusInternal.get()}"
    }
  }

  @CallSuper
  override fun onStop(owner: Context) {
    require(statusInternal.compareAndSet(LifecycleStatus.STARTED, LifecycleStatus.CREATED)) {
      "Unexpected lifecycle status: onStop called, but status is not RESUMED: ${statusInternal.get()}"
    }
  }

  @CallSuper
  override fun onDestroy(owner: Context) {
    require(statusInternal.compareAndSet(LifecycleStatus.CREATED, LifecycleStatus.DESTROYED)) {
      "Unexpected lifecycle status: onDestroy called, but status is not RESUMED: ${statusInternal.get()}"
    }
  }
}
