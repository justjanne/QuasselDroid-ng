package de.kuschku.quasseldroid.util.service

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import de.kuschku.libquassel.session.Backend
import de.kuschku.quasseldroid.service.QuasselService

class BackendServiceConnection : ServiceConnection {
  val backend = MutableLiveData<Backend?>()

  var context: Context? = null

  override fun onServiceDisconnected(component: ComponentName?) {
    when (component) {
      ComponentName(context, QuasselService::class.java) -> {
        backend.value = null
      }
    }
  }

  override fun onServiceConnected(component: ComponentName?, binder: IBinder?) {
    when (component) {
      ComponentName(context, QuasselService::class.java) ->
        if (binder is QuasselService.QuasselBinder) {
          backend.value = binder.backend
        }
    }
  }

  fun start(intent: Intent = Intent(context, QuasselService::class.java)) {
    context?.startService(intent)
  }

  fun bind(intent: Intent = Intent(context, QuasselService::class.java), flags: Int = 0) {
    context?.bindService(intent, this, flags)
  }

  fun stop(intent: Intent = Intent(context, QuasselService::class.java)) {
    context?.stopService(intent)
  }

  fun unbind() {
    context?.unbindService(this)
  }
}