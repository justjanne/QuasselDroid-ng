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

package de.kuschku.quasseldroid.ui.coresettings.network

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.defaults.Defaults
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.ui.coresettings.networkserver.NetworkServerActivity
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData
import kotlin.math.roundToInt

abstract class NetworkBaseFragment(private val initDefault: Boolean) :
  SettingsFragment(), SettingsFragment.Savable, SettingsFragment.Changeable {
  @BindView(R.id.network_name)
  lateinit var networkName: EditText

  @BindView(R.id.identity)
  lateinit var identity: Spinner

  @BindView(R.id.servers)
  lateinit var servers: RecyclerView

  @BindView(R.id.new_server)
  lateinit var newServer: Button

  @BindView(R.id.sasl_enabled)
  lateinit var saslEnabled: SwitchCompat

  @BindView(R.id.sasl_group)
  lateinit var saslGroup: ViewGroup

  @BindView(R.id.sasl_account)
  lateinit var saslAccount: EditText

  @BindView(R.id.sasl_password)
  lateinit var saslPassword: EditText

  @BindView(R.id.autoidentify_enabled)
  lateinit var autoidentifyEnabled: SwitchCompat

  @BindView(R.id.autoidentify_group)
  lateinit var autoidentifyGroup: ViewGroup

  @BindView(R.id.autoidentify_service)
  lateinit var autoidentifyService: EditText

  @BindView(R.id.autoidentify_password)
  lateinit var autoidentifyPassword: EditText

  @BindView(R.id.autoreconnect_enabled)
  lateinit var autoreconnectEnabled: SwitchCompat

  @BindView(R.id.autoreconnect_group)
  lateinit var autoreconnectGroup: ViewGroup

  @BindView(R.id.autoreconnect_interval)
  lateinit var autoreconnectInterval: EditText

  @BindView(R.id.autoreconnect_attempts)
  lateinit var autoreconnectRetries: EditText

  @BindView(R.id.autoreconnect_unlimited)
  lateinit var autoreconnectUnlimited: SwitchCompat

  @BindView(R.id.perform)
  lateinit var perform: EditText

  @BindView(R.id.rejoin_channels)
  lateinit var rejoinChannels: SwitchCompat

  @BindView(R.id.customratelimits_enabled)
  lateinit var customratelimitsEnabled: SwitchCompat

  @BindView(R.id.customratelimits_group)
  lateinit var customratelimitsGroup: ViewGroup

  @BindView(R.id.customratelimits_burstsize)
  lateinit var customratelimitsBurstSize: EditText

  @BindView(R.id.customratelimits_unlimited)
  lateinit var customratelimitsUnlimited: SwitchCompat

  @BindView(R.id.customratelimits_delay)
  lateinit var customratelimitsDelay: EditText

  protected var network: Pair<Network?, Network>? = null

  private lateinit var adapter: NetworkServerAdapter
  private lateinit var helper: ItemTouchHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_network, container, false)
    ButterKnife.bind(this, view)

    val networkId = arguments?.getInt("network", -1) ?: -1

    adapter = NetworkServerAdapter(::serverClick, ::startDrag)
    servers.layoutManager = LinearLayoutManager(requireContext())
    servers.adapter = adapter
    ViewCompat.setNestedScrollingEnabled(servers, false)

    val callback = DragSortItemTouchHelperCallback(adapter)
    helper = ItemTouchHelper(callback)
    helper.attachToRecyclerView(servers)

    newServer.setOnClickListener {
      startActivityForResult(
        NetworkServerActivity.intent(requireContext()),
        REQUEST_CREATE_SERVER
      )
    }

    val identityAdapter = IdentityAdapter()
    identity.adapter = identityAdapter

    viewModel.identities.switchMap {
      combineLatest(it.values.map(Identity::liveUpdates)).map {
        it.sortedBy(Identity::identityName)
      }
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        val selectOriginal = identity.selectedItemId == Spinner.INVALID_ROW_ID
        identityAdapter.submitList(it)
        if (selectOriginal) {
          this.network?.let { (_, data) ->
            identityAdapter.indexOf(data.networkId())?.let(identity::setSelection)
          }
        }
      }
    })

    if (initDefault) {
      viewModel.session
        .filter(Optional<ISession>::isPresent)
        .map(Optional<ISession>::get)
        .firstElement()
        .toLiveData().observe(this, Observer {
          it?.let {
            update(Defaults.network(requireContext(), it.proxy), identityAdapter)
          }
        })
    } else {
      viewModel.networks.map { Optional.ofNullable(it[networkId]) }
        .filter(Optional<Network>::isPresent)
        .map(Optional<Network>::get)
        .firstElement()
        .toLiveData().observe(this, Observer {
          it?.let {
            update(it, identityAdapter)
          }
        })
    }

    saslEnabled.setDependent(saslGroup)
    autoidentifyEnabled.setDependent(autoidentifyGroup)
    autoreconnectEnabled.setDependent(autoreconnectGroup)
    autoreconnectUnlimited.setOnCheckedChangeListener { _, isChecked ->
      autoreconnectRetries.isEnabled = !isChecked
    }
    customratelimitsEnabled.setDependent(customratelimitsGroup)
    customratelimitsUnlimited.setOnCheckedChangeListener { _, isChecked ->
      customratelimitsBurstSize.isEnabled = !isChecked
      customratelimitsDelay.isEnabled = !isChecked
    }
    customratelimitsBurstSize.isEnabled = !customratelimitsUnlimited.isChecked
    customratelimitsDelay.isEnabled = !customratelimitsUnlimited.isChecked

    return view
  }

  private fun update(it: Network, identityAdapter: IdentityAdapter) {
    if (this.network == null) {
      this.network = Pair(it, it.copy())
      this.network?.let { (_, data) ->
        networkName.setText(data.networkName())

        identityAdapter.indexOf(data.identity())?.let(identity::setSelection)

        adapter.list = data.serverList()

        saslEnabled.isChecked = data.useSasl()
        saslAccount.setText(data.saslAccount())
        saslPassword.setText(data.saslPassword())

        autoidentifyEnabled.isChecked = data.useAutoIdentify()
        autoidentifyService.setText(data.autoIdentifyService())
        autoidentifyPassword.setText(data.autoIdentifyPassword())

        autoreconnectEnabled.isChecked = data.useAutoReconnect()
        autoreconnectInterval.setText(data.autoReconnectInterval().toString())
        autoreconnectRetries.setText(data.autoReconnectRetries().toString())
        autoreconnectUnlimited.isChecked = data.unlimitedReconnectRetries()

        perform.setText(data.perform().joinToString("\n"))
        rejoinChannels.isChecked = data.rejoinChannels()

        customratelimitsEnabled.isChecked = data.useCustomMessageRate()
        customratelimitsBurstSize.setText(data.messageRateBurstSize().toString())
        customratelimitsUnlimited.isChecked = data.unlimitedMessageRate()
        customratelimitsDelay.setText("${data.messageRateDelay() / 1000.0}")
      }
    }
  }

  private fun serverClick(server: INetwork.Server) {
    startActivityForResult(
      NetworkServerActivity.intent(requireContext(), server = server),
      REQUEST_UPDATE_SERVER
    )
  }

  private fun startDrag(holder: RecyclerView.ViewHolder) {
    helper.startDrag(holder)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && data != null) {
      when (requestCode) {
        REQUEST_UPDATE_SERVER -> {
          val old = data.getSerializableExtra("old") as? INetwork.Server
          val new = data.getSerializableExtra("new") as? INetwork.Server
          if (old != null && new != null) {
            adapter.replace(old, new)
          }
        }
        REQUEST_CREATE_SERVER -> {
          val new = data.getSerializableExtra("new") as? INetwork.Server
          if (new != null) {
            adapter.add(new)
          }
        }
      }
    }
  }

  override fun hasChanged() = network?.let { (it, data) ->
    applyChanges(data)
    it == null || !data.isEqual(it)
  } ?: true

  protected fun applyChanges(data: Network) {
    data.setNetworkName(networkName.text.toString())

    data.setIdentity(identity.selectedItemId.toInt())

    data.setActualServerList(adapter.list)

    data.setUseSasl(saslEnabled.isChecked)
    data.setSaslAccount(saslAccount.text.toString())
    data.setSaslPassword(saslPassword.text.toString())

    data.setUseAutoIdentify(autoidentifyEnabled.isChecked)
    data.setAutoIdentifyService(autoidentifyService.text.toString())
    data.setAutoIdentifyPassword(autoidentifyPassword.text.toString())

    data.setUseAutoReconnect(autoreconnectEnabled.isChecked)
    data.setAutoReconnectInterval(autoreconnectInterval.text.toString().toIntOrNull()
                                  ?: data.autoReconnectInterval())
    data.setAutoReconnectRetries(autoreconnectRetries.text.toString().toShortOrNull()
                                 ?: data.autoReconnectRetries())
    data.setUnlimitedReconnectRetries(autoreconnectUnlimited.isChecked)

    data.setPerform(perform.text.lines())
    data.setRejoinChannels(rejoinChannels.isChecked)

    data.setUseCustomMessageRate(customratelimitsEnabled.isChecked)
    data.setMessageRateBurstSize(customratelimitsBurstSize.text.toString().toIntOrNull()
                                 ?: data.messageRateBurstSize())
    data.setUnlimitedMessageRate(customratelimitsUnlimited.isChecked)
    data.setMessageRateDelay(customratelimitsDelay.toString().toFloatOrNull()
                               ?.let { (it * 1000).roundToInt() }
                             ?: data.messageRateDelay())
  }

  companion object {
    private const val REQUEST_UPDATE_SERVER = 1
    private const val REQUEST_CREATE_SERVER = 2
  }
}
