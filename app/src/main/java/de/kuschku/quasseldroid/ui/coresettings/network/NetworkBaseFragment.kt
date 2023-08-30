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
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.libquassel.util.helper.combineLatest
import de.kuschku.libquassel.util.helper.nullIf
import de.kuschku.libquassel.util.helper.safeSwitchMap
import de.kuschku.libquassel.util.helper.value
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.defaults.Defaults
import de.kuschku.quasseldroid.ui.coresettings.networkserver.NetworkServerActivity
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.helper.visibleIf
import de.kuschku.quasseldroid.util.ui.settings.fragment.Changeable
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment
import de.kuschku.quasseldroid.util.ui.view.InlineSnackBar
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject
import kotlin.math.roundToInt

abstract class NetworkBaseFragment(private val initDefault: Boolean) :
  ServiceBoundSettingsFragment(), Savable, Changeable {
  lateinit var networkName: EditText
  lateinit var identity: Spinner
  lateinit var servers: RecyclerView
  lateinit var newServer: Button
  lateinit var saslEnabled: SwitchCompat
  lateinit var saslGroup: ViewGroup
  lateinit var saslAccount: EditText
  lateinit var saslPassword: EditText
  lateinit var autoidentifyEnabled: SwitchCompat
  lateinit var autoidentifyGroup: ViewGroup
  lateinit var autoidentifyWarning: InlineSnackBar
  lateinit var autoidentifyService: EditText
  lateinit var autoidentifyPassword: EditText
  lateinit var autoreconnectEnabled: SwitchCompat
  lateinit var autoreconnectGroup: ViewGroup
  lateinit var autoreconnectInterval: EditText
  lateinit var autoreconnectRetries: EditText
  lateinit var autoreconnectUnlimited: SwitchCompat
  lateinit var perform: EditText
  lateinit var rejoinChannels: SwitchCompat
  lateinit var customratelimitsEnabled: SwitchCompat
  lateinit var customratelimitsGroup: ViewGroup
  lateinit var customratelimitsBurstSize: EditText
  lateinit var customratelimitsUnlimited: SwitchCompat
  lateinit var customratelimitsDelay: EditText

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  protected var network: Pair<Network?, Network>? = null

  private lateinit var adapter: NetworkServerAdapter
  private lateinit var helper: ItemTouchHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_network, container, false)
    this.networkName = view.findViewById(R.id.network_name)
    this.identity = view.findViewById(R.id.identity)
    this.servers = view.findViewById(R.id.servers)
    this.newServer = view.findViewById(R.id.new_server)
    this.saslEnabled = view.findViewById(R.id.sasl_enabled)
    this.saslGroup = view.findViewById(R.id.sasl_group)
    this.saslAccount = view.findViewById(R.id.sasl_account)
    this.saslPassword = view.findViewById(R.id.sasl_password)
    this.autoidentifyEnabled = view.findViewById(R.id.autoidentify_enabled)
    this.autoidentifyGroup = view.findViewById(R.id.autoidentify_group)
    this.autoidentifyWarning = view.findViewById(R.id.autoidentify_warning)
    this.autoidentifyService = view.findViewById(R.id.autoidentify_service)
    this.autoidentifyPassword = view.findViewById(R.id.autoidentify_password)
    this.autoreconnectEnabled = view.findViewById(R.id.autoreconnect_enabled)
    this.autoreconnectGroup = view.findViewById(R.id.autoreconnect_group)
    this.autoreconnectInterval = view.findViewById(R.id.autoreconnect_interval)
    this.autoreconnectRetries = view.findViewById(R.id.autoreconnect_attempts)
    this.autoreconnectUnlimited = view.findViewById(R.id.autoreconnect_unlimited)
    this.perform = view.findViewById(R.id.perform)
    this.rejoinChannels = view.findViewById(R.id.rejoin_channels)
    this.customratelimitsEnabled = view.findViewById(R.id.customratelimits_enabled)
    this.customratelimitsGroup = view.findViewById(R.id.customratelimits_group)
    this.customratelimitsBurstSize = view.findViewById(R.id.customratelimits_burstsize)
    this.customratelimitsUnlimited = view.findViewById(R.id.customratelimits_unlimited)
    this.customratelimitsDelay = view.findViewById(R.id.customratelimits_delay)

    val networkId = NetworkId(arguments?.getInt("network", -1) ?: -1)

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

    modelHelper.identities.safeSwitchMap {
      combineLatest(it.values.map(Identity::liveUpdates)).map {
        it.sortedBy(Identity::identityName)
      }
    }.toLiveData().observe(viewLifecycleOwner, Observer {
      if (it != null) {
        val selectOriginal = identity.selectedItemId == Spinner.INVALID_ROW_ID
        identityAdapter.submitList(it)
        if (selectOriginal) {
          this.network?.let { (_, data) ->
            identityAdapter.indexOf(data.identity())?.let(identity::setSelection)
          }
        }
      }
    })

    if (initDefault) {
      modelHelper.connectedSession
        .filter(Optional<ISession>::isPresent)
        .map(Optional<ISession>::get)
        .firstElement()
        .toLiveData().observe(viewLifecycleOwner, Observer {
          it?.let {
            update(Defaults.network(requireContext(), it.proxy), identityAdapter)
          }
        })
    } else {
      modelHelper.networks.map { Optional.ofNullable(it[networkId]) }
        .filter(Optional<Network>::isPresent)
        .map(Optional<Network>::get)
        .firstElement()
        .toLiveData()
        .observe(viewLifecycleOwner, Observer {
          it?.let {
            update(it, identityAdapter)
          }
        })
      modelHelper.networks.map { Optional.ofNullable(it[networkId]) }
        .filter(Optional<Network>::isPresent)
        .map(Optional<Network>::get)
        .safeSwitchMap(Network::liveCaps)
        .toLiveData()
        .observe(viewLifecycleOwner, Observer {
          autoidentifyWarning.visibleIf(it.contains("sasl"))
        })
    }


    autoidentifyWarning.setOnClickListener {
      val identity = modelHelper.identities.value?.get(IdentityId(identity.selectedItemId.toInt()))
      if (identity != null) {
        saslEnabled.isChecked = true
        saslAccount.setText(identity.nicks().firstOrNull())
        saslPassword.setText(autoidentifyPassword.text.toString())
        autoidentifyEnabled.isChecked = false
      }
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
        customratelimitsDelay.setText("${data.messageRateDelay().toInt() / 1000.0}")
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

    data.setIdentity(IdentityId(identity.selectedItemId.toInt()))

    data.setActualServerList(adapter.list)

    data.setUseSasl(saslEnabled.isChecked)
    data.setSaslAccount(saslAccount.text.toString())
    data.setSaslPassword(saslPassword.text.toString())

    data.setUseAutoIdentify(autoidentifyEnabled.isChecked)
    data.setAutoIdentifyService(autoidentifyService.text.toString())
    data.setAutoIdentifyPassword(autoidentifyPassword.text.toString())

    data.setUseAutoReconnect(autoreconnectEnabled.isChecked)
    data.setAutoReconnectInterval(autoreconnectInterval.text.toString().toUIntOrNull()
                                  ?: data.autoReconnectInterval())
    data.setAutoReconnectRetries(autoreconnectRetries.text.toString().toUShortOrNull()
                                 ?: data.autoReconnectRetries())
    data.setUnlimitedReconnectRetries(autoreconnectUnlimited.isChecked)

    data.setPerform(perform.text.lines())
    data.setRejoinChannels(rejoinChannels.isChecked)

    data.setUseCustomMessageRate(customratelimitsEnabled.isChecked)
    data.setMessageRateBurstSize(customratelimitsBurstSize.text.toString().toUIntOrNull()
                                 ?: data.messageRateBurstSize())
    data.setUnlimitedMessageRate(customratelimitsUnlimited.isChecked)
    data.setMessageRateDelay(customratelimitsDelay.toString().toFloatOrNull()
                               ?.let { (it * 1000).roundToInt() }
                               ?.nullIf { it < 0 }
                               ?.toUInt()
                             ?: data.messageRateDelay())
  }

  companion object {
    private const val REQUEST_UPDATE_SERVER = 1
    private const val REQUEST_CREATE_SERVER = 2
  }
}
