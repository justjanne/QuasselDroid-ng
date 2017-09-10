package de.kuschku.quasseldroid_ng.session

import android.arch.lifecycle.LiveData

interface Backend {
  fun connect(address: SocketAddress, user: String, pass: String)
  fun disconnect()
  fun session(): Session
  val status: LiveData<ConnectionState>
}
