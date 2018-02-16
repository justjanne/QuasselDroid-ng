package de.kuschku.quasseldroid_ng.util.service

import android.arch.lifecycle.LiveData
import android.os.Bundle
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v7.app.AppCompatActivity
import de.kuschku.libquassel.session.Backend
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.updateRecentsHeaderIfExisting

abstract class ServiceBoundActivity : AppCompatActivity() {
  @DrawableRes
  protected val icon: Int = R.mipmap.ic_launcher_recents
  @ColorRes
  protected val recentsHeaderColor: Int = R.color.colorPrimary

  private val connection = BackendServiceConnection()

  val backend: LiveData<Backend?>
    get() = connection.backend

  override fun onCreate(savedInstanceState: Bundle?) {
    connection.context = this
    setTheme(R.style.Theme_ChatTheme_Quassel_Light)
    super.onCreate(savedInstanceState)
    connection.start()
    updateRecentsHeader()
  }

  fun updateRecentsHeader()
    = updateRecentsHeaderIfExisting(title.toString(), icon, recentsHeaderColor)

  override fun setTitle(title: CharSequence?) {
    super.setTitle(title)
    updateRecentsHeader()
  }

  override fun onStart() {
    connection.bind()
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    connection.unbind()
  }

  protected fun stopService() {
    connection.unbind()
    connection.stop()
  }
}
