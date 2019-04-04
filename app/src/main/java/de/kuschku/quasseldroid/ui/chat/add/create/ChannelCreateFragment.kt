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

package de.kuschku.quasseldroid.ui.chat.add.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment

class ChannelCreateFragment : ServiceBoundSettingsFragment(), Savable {
  @BindView(R.id.network)
  lateinit var network: AppCompatSpinner

  @BindView(R.id.name)
  lateinit var name: EditText

  @BindView(R.id.hidden)
  lateinit var hidden: SwitchCompat

  @BindView(R.id.invite_only)
  lateinit var inviteOnly: SwitchCompat

  @BindView(R.id.password_protected)
  lateinit var passwordProtected: SwitchCompat

  @BindView(R.id.password_group)
  lateinit var passwordGroup: ViewGroup

  @BindView(R.id.password)
  lateinit var password: EditText

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.add_create, container, false)
    ButterKnife.bind(this, view)

    val networkAdapter = NetworkAdapter()
    network.adapter = networkAdapter

    viewModel.networks.switchMap {
      combineLatest(it.values.map(Network::liveNetworkInfo)).map {
        it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, INetwork.NetworkInfo::networkName))
      }
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        networkAdapter.submitList(it)
      }
    })

    passwordProtected.setDependent(passwordGroup)

    return view
  }

  override fun onSave(): Boolean {
    // TODO: Implement
    return false
  }
}
