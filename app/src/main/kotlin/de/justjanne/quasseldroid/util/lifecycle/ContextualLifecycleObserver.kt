package de.justjanne.quasseldroid.util.lifecycle

import android.content.Context

interface ContextualLifecycleObserver {
  fun onCreate(owner: Context) = Unit
  fun onStart(owner: Context) = Unit
  fun onResume(owner: Context) = Unit
  fun onPause(owner: Context) = Unit
  fun onStop(owner: Context) = Unit
  fun onDestroy(owner: Context) = Unit
}
