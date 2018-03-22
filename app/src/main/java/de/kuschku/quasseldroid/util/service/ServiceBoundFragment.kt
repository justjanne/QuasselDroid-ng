package de.kuschku.quasseldroid.util.service

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.Bundle
import dagger.android.support.DaggerFragment
import de.kuschku.libquassel.session.Backend
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.util.helper.invoke

abstract class ServiceBoundFragment : DaggerFragment() {
  private var connection = BackendServiceConnection()

  protected val backend: LiveData<Backend?>
    get() = connection.backend

  protected fun runInBackground(f: () -> Unit) {
    connection.backend {
      it.sessionManager().handlerService.backend(f)
    }
  }

  protected fun runInBackgroundDelayed(delayMillis: Long, f: () -> Unit) {
    connection.backend {
      it.sessionManager().handlerService.backendDelayed(delayMillis, f)
    }
  }

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
