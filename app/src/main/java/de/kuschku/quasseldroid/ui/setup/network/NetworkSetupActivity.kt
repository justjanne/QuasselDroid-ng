/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.setup.network

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.helpers.value
import de.kuschku.quasseldroid.ui.setup.ServiceBoundSetupActivity
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject

class NetworkSetupActivity : ServiceBoundSetupActivity() {
  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  private lateinit var arguments: Bundle
  override val initData: Bundle
    get() = arguments

  override fun onCreate(savedInstanceState: Bundle?) {
    arguments = intent.getBundleExtra("link")
    super.onCreate(savedInstanceState)
  }

  override fun onDone(data: Bundle) {
    val network = data.getSerializable("network") as? LinkNetwork
    val networkId = NetworkId(data.getInt("network_id", -1))
    val identity = IdentityId(data.getInt("identity", -1))
    if (networkId.isValidId() || (network != null && identity.isValidId())) {
      modelHelper.backend?.value?.ifPresent { backend ->
        val session = modelHelper.session.value?.orNull()
        session?.apply {
          rpcHandler.apply {
            when {
              networkId.isValidId()            -> {
                val buffer = bufferSyncer.find(networkId = networkId,
                                               type = Buffer_Type.of(Buffer_Type.StatusBuffer))
                if (buffer != null) {
                  data.getStringArray("channels")?.toList().orEmpty().forEach {
                    sendInput(buffer, "/join $it")
                  }
                }
              }
              network != null &&
              network.name.isNotBlank() &&
              network.server.host.isNotBlank() -> {
                createNetwork(INetwork.NetworkInfo(
                  networkName = network.name,
                  identity = identity,
                  serverList = listOf(INetwork.Server(
                    host = network.server.host,
                    port = network.server.port,
                    useSsl = network.server.secure
                  ))
                ), data.getStringArray("channels")?.toList().orEmpty())
                backend.requestConnectNewNetwork()
              }
            }
          }
        }
      }
    }

    setResult(Activity.RESULT_OK)
    finish()
  }

  override val fragments = listOf(
    NetworkSetupNetworkSlide(),
    NetworkSetupChannelsSlide()
  )

  companion object {
    fun launch(
      context: Context,
      network: LinkNetwork? = null,
      identity: IdentityId? = null,
      channels: Array<String>? = null
    ) = context.startActivity(intent(context, network, identity, channels))

    fun intent(
      context: Context,
      network: LinkNetwork? = null,
      identity: IdentityId? = null,
      channels: Array<String>? = null
    ) = Intent(context, NetworkSetupActivity::class.java).apply {
      if (network != null || identity != null || channels != null) {
        val bundle = Bundle()
        if (network != null) {
          bundle.putSerializable("network", network)
        }
        if (identity != null) {
          bundle.putInt("identity", identity.id)
        }
        if (channels != null) {
          bundle.putStringArray("channels", channels)
        }
        putExtra("link", bundle)
      }
    }
  }
}
