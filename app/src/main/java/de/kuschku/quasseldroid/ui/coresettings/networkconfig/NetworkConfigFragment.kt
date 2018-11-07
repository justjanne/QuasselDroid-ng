/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.coresettings.networkconfig

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.NetworkConfig
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData

class NetworkConfigFragment : SettingsFragment(), SettingsFragment.Savable,
                              SettingsFragment.Changeable {
  @BindView(R.id.ping_timeout_enabled)
  lateinit var pingTimeoutEnabled: SwitchCompat

  @BindView(R.id.ping_timeout_group)
  lateinit var pingTimeoutGroup: ViewGroup

  @BindView(R.id.ping_interval)
  lateinit var pingInterval: EditText

  @BindView(R.id.max_ping_count)
  lateinit var maxPingCount: EditText

  @BindView(R.id.auto_who_enabled)
  lateinit var autoWhoEnabled: SwitchCompat

  @BindView(R.id.auto_who_group)
  lateinit var autoWhoGroup: ViewGroup

  @BindView(R.id.auto_who_interval)
  lateinit var autoWhoInterval: EditText

  @BindView(R.id.auto_who_nick_limit)
  lateinit var autoWhoNickLimit: EditText

  @BindView(R.id.auto_who_delay)
  lateinit var autoWhoDelay: EditText

  @BindView(R.id.standard_ctcp)
  lateinit var standardCtcp: SwitchCompat

  private var networkConfig: Pair<NetworkConfig, NetworkConfig>? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_networkconfig, container, false)
    ButterKnife.bind(this, view)

    viewModel.networkConfig
      .filter(Optional<NetworkConfig>::isPresent)
      .map(Optional<NetworkConfig>::get)
      .firstElement()
      .toLiveData().observe(this, Observer {
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
