package de.kuschku.quasseldroid_ng.util.service

import android.arch.lifecycle.LiveData
import android.os.Bundle
import android.support.v4.app.Fragment
import de.kuschku.libquassel.session.Backend

abstract class ServiceBoundFragment : Fragment() {
  private var connection = BackendServiceConnection()

  val backend: LiveData<Backend?>
    get() = connection.backend

  override fun onCreate(savedInstanceState: Bundle?) {
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
