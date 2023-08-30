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

package de.kuschku.quasseldroid.ui.coresettings.networkconfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import de.kuschku.libquassel.quassel.syncables.NetworkConfig
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.settings.fragment.Changeable
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject

class NetworkConfigFragment : ServiceBoundSettingsFragment(), Savable,
                              Changeable {
  lateinit var pingTimeoutEnabled: SwitchCompat
  lateinit var pingTimeoutGroup: ViewGroup
  lateinit var pingInterval: EditText
  lateinit var maxPingCount: EditText
  lateinit var autoWhoEnabled: SwitchCompat
  lateinit var autoWhoGroup: ViewGroup
  lateinit var autoWhoInterval: EditText
  lateinit var autoWhoNickLimit: EditText
  lateinit var autoWhoDelay: EditText
  lateinit var standardCtcp: SwitchCompat

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  private var networkConfig: Pair<NetworkConfig, NetworkConfig>? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_networkconfig, container, false)
    this.pingTimeoutEnabled = view.findViewById(R.id.ping_timeout_enabled)
    this.pingTimeoutGroup = view.findViewById(R.id.ping_timeout_group)
    this.pingInterval = view.findViewById(R.id.ping_interval)
    this.maxPingCount = view.findViewById(R.id.max_ping_count)
    this.autoWhoEnabled = view.findViewById(R.id.auto_who_enabled)
    this.autoWhoGroup = view.findViewById(R.id.auto_who_group)
    this.autoWhoInterval = view.findViewById(R.id.auto_who_interval)
    this.autoWhoNickLimit = view.findViewById(R.id.auto_who_nick_limit)
    this.autoWhoDelay = view.findViewById(R.id.auto_who_delay)
    this.standardCtcp = view.findViewById(R.id.standard_ctcp)

    modelHelper.networkConfig
      .filter(Optional<NetworkConfig>::isPresent)
      .map(Optional<NetworkConfig>::get)
      .firstElement()
      .toLiveData().observe(viewLifecycleOwner, Observer {
        it?.let {
          if (this.networkConfig == null) {
            this.networkConfig = Pair(it, it.copy())
            this.networkConfig?.let { (_, data) ->
              pingTimeoutEnabled.isChecked = data.pingTimeoutEnabled()
              pingInterval.setText(data.pingInterval().toString())
              maxPingCount.setText(data.maxPingCount().toString())

              autoWhoEnabled.isChecked = data.autoWhoEnabled()
              autoWhoInterval.setText(data.autoWhoInterval().toString())
              autoWhoNickLimit.setText(data.autoWhoNickLimit().toString())
              autoWhoDelay.setText(data.autoWhoDelay().toString())

              standardCtcp.isChecked = data.standardCtcp()
            }
          }
        }
      })

    pingTimeoutEnabled.setDependent(pingTimeoutGroup)
    autoWhoEnabled.setDependent(autoWhoGroup)

    return view
  }

  override fun onSave() = networkConfig?.let { (it, data) ->
    applyChanges(data)

    it.requestUpdate(data.toVariantMap())
    true
  } ?: false

  override fun hasChanged() = networkConfig?.let { (it, data) ->
    applyChanges(data)

    data.pingTimeoutEnabled() != it.pingTimeoutEnabled() ||
    data.pingInterval() != it.pingInterval() ||
    data.maxPingCount() != it.maxPingCount() ||
    data.autoWhoEnabled() != it.autoWhoEnabled() ||
    data.autoWhoInterval() != it.autoWhoInterval() ||
    data.autoWhoNickLimit() != it.autoWhoNickLimit() ||
    data.autoWhoDelay() != it.autoWhoDelay() ||
    data.standardCtcp() != it.standardCtcp()
  } ?: false

  private fun applyChanges(data: NetworkConfig) {
    data.setPingTimeoutEnabled(pingTimeoutEnabled.isChecked)
    pingInterval.text.toString().toIntOrNull()?.let(data::setPingInterval)
    maxPingCount.text.toString().toIntOrNull()?.let(data::setMaxPingCount)

    data.setAutoWhoEnabled(autoWhoEnabled.isChecked)
    autoWhoInterval.text.toString().toIntOrNull()?.let(data::setAutoWhoInterval)
    autoWhoNickLimit.text.toString().toIntOrNull()?.let(data::setAutoWhoNickLimit)
    autoWhoDelay.text.toString().toIntOrNull()?.let(data::setAutoWhoDelay)
    data.setStandardCtcp(standardCtcp.isChecked)
  }
}
