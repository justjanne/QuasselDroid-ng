package de.kuschku.quasseldroid_ng.ui

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import de.kuschku.libquassel.session.Backend
import de.kuschku.quasseldroid_ng.service.QuasselService

abstract class ServiceBoundActivity : AppCompatActivity() {
  protected val backend = MutableLiveData<Backend?>()

  private val connection = object : ServiceConnection {
    override fun onServiceDisconnected(component: ComponentName?) {
      when (component) {
        ComponentName(application, QuasselService::class.java) -> {
          backend.value = null
        }
      }
    }

    override fun onServiceConnected(component: ComponentName?, binder: IBinder?) {
      when (component) {
        ComponentName(application, QuasselService::class.java) ->
          if (binder is QuasselService.QuasselBinder) {
            backend.value = binder.backend
          }
      }
    }
  }

  override fun onStart() {
    bindService(Intent(this, QuasselService::class.java), connection, 0)
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    unbindService(connection)
  }
}
