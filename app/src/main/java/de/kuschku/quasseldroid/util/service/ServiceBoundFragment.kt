package de.kuschku.quasseldroid.util.service

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import dagger.android.support.DaggerFragment
import de.kuschku.libquassel.session.Backend
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.viewmodel.QuasselViewModel
import io.reactivex.subjects.BehaviorSubject

abstract class ServiceBoundFragment : DaggerFragment() {
  private val connection = BackendServiceConnection()
  protected val backend: BehaviorSubject<Optional<Backend>>
    get() = connection.backend

  protected lateinit var viewModel: QuasselViewModel

  protected fun runInBackground(f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager().handlerService.backend(f)
    }
  }

  protected fun runInBackgroundDelayed(delayMillis: Long, f: () -> Unit) {
    connection.backend.value.ifPresent {
      it.sessionManager().handlerService.backendDelayed(delayMillis, f)
    }
  }

  protected var accountId: Long = -1

  override fun onAttach(context: Context?) {
    super.onAttach(context)
    viewModel = ViewModelProviders.of(requireActivity())[QuasselViewModel::class.java]
  }

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
