package de.kuschku.quasseldroid.util.service

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import de.kuschku.libquassel.session.Backend
import de.kuschku.quasseldroid.Keys

abstract class ServiceBoundFragment : Fragment() {
  private var connection = BackendServiceConnection()

  protected val backend: LiveData<Backend?>
    get() = connection.backend

  protected var accountId: Long = -1

  override fun onCreate(savedInstanceState: Bundle?) {
    accountId = context?.getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
      ?.getLong(Keys.Status.selectedAccount, -1) ?: -1

    connection.context = context
    super.onCreate(savedInstanceState)
    connection.start()
  }

  override fun onStart() {
    connection.bind()
    super.onStart()
  }

  override fun onStop() {
    super.onStop()
    connection.unbind()
  }
}
