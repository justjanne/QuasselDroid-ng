package de.kuschku.quasseldroid_ng.util.service

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import de.kuschku.libquassel.session.Backend
import de.kuschku.quasseldroid_ng.service.QuasselService

abstract class ServiceBoundFragment : Fragment() {
  protected val backend = MutableLiveData<Backend?>()

  private val connection = object : ServiceConnection {
    override fun onServiceDisconnected(component: ComponentName?) {
      when (component) {
        ComponentName(context.applicationContext, QuasselService::class.java) -> {
          backend.value = null
        }
      }
    }

    override fun onServiceConnected(component: ComponentName?, binder: IBinder?) {
      when (component) {
        ComponentName(context.applicationContext, QuasselService::class.java) ->
          if (binder is QuasselService.QuasselBinder) {
            backend.value = binder.backend
          }
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    context.startService(Intent(context, QuasselService::class.java))
  }

  override fun onStart() {
    context.bindService(Intent(context, QuasselService::class.java), connection, 0)
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    context.unbindService(connection)
  }
}
