package de.kuschku.quasseldroid.util.backport

import android.arch.lifecycle.LifecycleService
import dagger.android.AndroidInjection

abstract class DaggerLifecycleService : LifecycleService() {
  override fun onCreate() {
    AndroidInjection.inject(this)
    super.onCreate()
  }
}
