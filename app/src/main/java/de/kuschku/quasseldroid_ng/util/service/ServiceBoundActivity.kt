package de.kuschku.quasseldroid_ng.util.service

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import de.kuschku.libquassel.session.Backend
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.service.QuasselService
import de.kuschku.quasseldroid_ng.util.helper.updateRecentsHeaderIfExisting

abstract class ServiceBoundActivity : AppCompatActivity() {
  protected val backend = MutableLiveData<Backend?>()
  @DrawableRes
  protected val icon: Int = R.mipmap.ic_launcher
  @ColorRes
  protected val recentsHeaderColor: Int = R.color.colorPrimaryDark

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

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_ChatTheme_Quassel_Light)
    super.onCreate(savedInstanceState)
    startService(Intent(this, QuasselService::class.java))
    updateRecentsHeader()
  }

  fun updateRecentsHeader()
    = updateRecentsHeaderIfExisting(title.toString(), icon, recentsHeaderColor)

  override fun setTitle(title: CharSequence?) {
    super.setTitle(title)
    updateRecentsHeader()
  }

  override fun onStart() {
    bindService(Intent(this, QuasselService::class.java), connection, 0)
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    unbindService(connection)
  }

  protected fun stopService() {
    unbindService(connection)
    stopService(Intent(this, QuasselService::class.java))
  }
}
