package de.justjanne.quasseldroid.service

import android.os.Binder
import de.justjanne.libquassel.client.session.ClientSession
import de.justjanne.libquassel.protocol.util.StateHolder
import kotlinx.coroutines.flow.StateFlow

class QuasselBinder(
  private val state: StateFlow<ClientSession?>
) : Binder(), StateHolder<ClientSession?> {
  constructor(runner: QuasselRunner) : this(runner.flow())

  override fun flow() = state
  override fun state() = state.value
}
