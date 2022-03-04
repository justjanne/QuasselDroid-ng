package de.justjanne.quasseldroid.service

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import de.justjanne.libquassel.client.session.ClientSession
import de.justjanne.libquassel.protocol.util.StateHolder
import de.justjanne.libquassel.protocol.util.flatMap
import de.justjanne.quasseldroid.BuildConfig
import de.justjanne.quasseldroid.service.QuasselService.Companion.quasselService
import de.justjanne.quasseldroid.util.lifecycle.DefaultContextualLifecycleObserver
import de.justjanne.quasseldroid.util.lifecycle.LifecycleStatus
import kotlinx.coroutines.flow.MutableStateFlow

class QuasselBackend : DefaultContextualLifecycleObserver(), ServiceConnection,
  StateHolder<ClientSession?> {
  private var connectionData: ConnectionData? = null

  override fun flow() = state.flatMap()
  override fun state() = state.value?.state()
  private val state = MutableStateFlow<QuasselBinder?>(null)

  override fun onCreate(owner: Context) {
    super.onCreate(owner)
    connectionData?.let { (address, username, password) ->
      Log.d("QuasselBackend", "Starting Quassel Service")
      owner.startService(owner.quasselService(address, Pair(username, password)))
    }
  }

  override fun onStart(owner: Context) {
    super.onStart(owner)
    connectionData?.let { (address, username, password) ->
      Log.d("QuasselBackend", "Binding Quassel Service")
      owner.bindService(owner.quasselService(address, Pair(username, password)), this, 0)
    }
  }

  override fun onStop(owner: Context) {
    super.onStop(owner)
    Log.d("QuasselBackend", "Unbinding Quassel Service")
    owner.unbindService(this)
  }

  fun login(context: Context, connectionData: ConnectionData): Boolean {
    this.connectionData = connectionData
    when (status) {
      LifecycleStatus.CREATED -> {
        val (address, username, password) = connectionData
        Log.d("QuasselBackend", "Starting Quassel Service")
        context.startService(
          context.quasselService(address, Pair(username, password))
        )
        return true
      }
      LifecycleStatus.STARTED, LifecycleStatus.RESUMED -> {
        val (address, username, password) = connectionData
        Log.d("QuasselBackend", "Binding Quassel Service")
        context.startService(
          context.quasselService(address, Pair(username, password))
        )
        context.bindService(
          context.quasselService(address, Pair(username, password)),
          this,
          0
        )
        return true
      }
      else -> {
        Log.w("QuasselBackend", "Trying to log in but status is $status")
        return false
      }
    }
  }

  fun disconnect(context: Context) {
    context.stopService(context.quasselService())
  }

  override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
    if (name == quasselService && service is QuasselBinder) {
      Log.d("QuasselBackend", "Quassel Service bound")
      state.value = service
    } else {
      Log.w("QuasselBackend", "Unknown Service bound: $name")
    }
  }

  override fun onServiceDisconnected(name: ComponentName?) {
    Log.d("QuasselBackend", "Service unbound: $name")
    state.value = null
  }

  companion object {
    private val quasselService = ComponentName(
      BuildConfig.APPLICATION_ID,
      QuasselService::class.java.canonicalName!!
    )
  }
}
