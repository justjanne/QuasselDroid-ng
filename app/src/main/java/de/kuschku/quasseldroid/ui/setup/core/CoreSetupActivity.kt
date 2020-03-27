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

package de.kuschku.quasseldroid.ui.setup.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.coresetup.CoreSetupBackend
import de.kuschku.libquassel.protocol.coresetup.CoreSetupData
import de.kuschku.libquassel.protocol.message.HandshakeMessage
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.util.helper.value
import de.kuschku.quasseldroid.persistence.models.Account
import de.kuschku.quasseldroid.ui.setup.ServiceBoundSetupActivity
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject

class CoreSetupActivity : ServiceBoundSetupActivity() {
  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  override val initData = Bundle()

  override fun onCreate(savedInstanceState: Bundle?) {
    initData.clear()
    initData.putAll(intent.extras)
    super.onCreate(savedInstanceState)
  }

  override fun onDone(data: Bundle) {
    val user = initData.getString("user")
    val pass = initData.getString("pass")

    val storageBackend = data.getSerializable("storage") as? CoreSetupBackend
    val storageBackendSetup = data.getSerializable("storageSetup") as? HashMap<String, QVariant_>

    val authenticatorBackend = data.getSerializable("authenticator") as? CoreSetupBackend
    val authenticatorBackendSetup = data.getSerializable("authenticatorSetup") as? HashMap<String, QVariant_>

    modelHelper.sessionManager.value?.orNull()?.setupCore(HandshakeMessage.CoreSetupData(
      adminUser = user,
      adminPassword = pass,
      backend = storageBackend?.backendId,
      setupData = storageBackendSetup.orEmpty(),
      authenticator = authenticatorBackend?.backendId,
      authSetupData = authenticatorBackendSetup.orEmpty()
    ))

    setResult(Activity.RESULT_OK)
    finish()
  }

  override val fragments
    get() = if ((initData.getSerializable("data") as? CoreSetupData)
        ?.features
        ?.hasFeature(ExtendedFeature.Authenticators) == true) {
      listOf(
        CoreStorageBackendChooseSlide(),
        CoreStorageBackendSetupSlide(),
        CoreAuthenticatorBackendChooseSlide(),
        CoreAuthenticatorBackendSetupSlide()
      )
    } else {
      listOf(
        CoreStorageBackendChooseSlide(),
        CoreStorageBackendSetupSlide()
      )
    }

  companion object {
    fun launch(
      context: Context,
      account: Account? = null,
      data: CoreSetupData? = null
    ) = context.startActivity(intent(context, account, data))

    fun intent(
      context: Context,
      account: Account? = null,
      data: CoreSetupData? = null
    ) = Intent(context, CoreSetupActivity::class.java).apply {
      if (account != null) {
        putExtra("user", account.user)
        putExtra("pass", account.pass)
      }
      if (data != null) {
        putExtra("data", data)
      }
    }
  }
}
