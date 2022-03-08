/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.quasseldroid.ui.setup.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.util.helper.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.defaults.DefaultNetwork
import de.kuschku.quasseldroid.defaults.Defaults
import de.kuschku.quasseldroid.ui.setup.ServiceBoundSetupActivity
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import java.util.*
import javax.inject.Inject

class UserSetupActivity : ServiceBoundSetupActivity() {
  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  override val initData
    get() = Bundle().apply {
      putString("nick", getString(R.string.default_identity_nick, Random().nextInt(16)))
      putString("realname", getString(R.string.default_identity_realname))
    }

  override fun onDone(data: Bundle) {
    val network = data.getSerializable("network") as? DefaultNetwork
    if (network != null) {
      modelHelper.backend.value?.ifPresent { backend ->
        modelHelper.connectedSession.value?.orNull()?.rpcHandler?.apply {
          createIdentity(Defaults.identity(this@UserSetupActivity).apply {
            setIdentityName(this@UserSetupActivity.getString(R.string.default_identity_identity_name))
            setNicks(listOf(data.getString("nick")))
            setRealName(data.getString("realname"))
          }.toVariantMap(), emptyMap())

          modelHelper.identities
            .map(Map<IdentityId, Identity>::values)
            .filter(Collection<Identity>::isNotEmpty)
            .map(Collection<Identity>::first)
            .firstElement()
            .toLiveData().observe(this@UserSetupActivity, Observer {
              if (it != null) {
                createNetwork(INetwork.NetworkInfo(
                  networkName = network.name,
                  identity = it.id(),
                  serverList = network.servers.map {
                    INetwork.Server(
                      host = it.host,
                      port = it.port,
                      useSsl = it.secure
                    )
                  }
                ).toVariantMap(), data.getStringArray("channels")?.toList().orEmpty())

                backend.requestConnectNewNetwork()
              }
            })
        }
      }
    }

    setResult(Activity.RESULT_OK)
    finish()
  }

  override val fragments = listOf(
    UserSetupIdentitySlide(),
    UserSetupNetworkSlide(),
    UserSetupChannelsSlide()
  )

  companion object {
    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, UserSetupActivity::class.java)
  }
}
