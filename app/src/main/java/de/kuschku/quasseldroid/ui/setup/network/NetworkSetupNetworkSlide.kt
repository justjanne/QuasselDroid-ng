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

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.protocol.NetworkId
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork.PortDefaults.PORT_PLAINTEXT
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork.PortDefaults.PORT_SSL
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.defaults.DefaultNetworkServer
import de.kuschku.quasseldroid.ui.coresettings.chatlist.NetworkAdapter
import de.kuschku.quasseldroid.ui.coresettings.network.IdentityAdapter
import de.kuschku.quasseldroid.ui.setup.ServiceBoundSlideFragment
import de.kuschku.quasseldroid.util.Patterns
import de.kuschku.quasseldroid.util.TextValidator
import de.kuschku.quasseldroid.util.helper.combineLatest
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.AnimationHelper

class NetworkSetupNetworkSlide : ServiceBoundSlideFragment() {
  @BindView(R.id.identity)
  lateinit var identity: Spinner

  @BindView(R.id.network)
  lateinit var network: Spinner

  @BindView(R.id.network_group)
  lateinit var networkGroup: ViewGroup

  @BindView(R.id.nameWrapper)
  lateinit var nameWrapper: TextInputLayout

  @BindView(R.id.name)
  lateinit var nameField: EditText

  @BindView(R.id.hostWrapper)
  lateinit var hostWrapper: TextInputLayout

  @BindView(R.id.host)
  lateinit var hostField: EditText

  @BindView(R.id.portWrapper)
  lateinit var portWrapper: TextInputLayout

  @BindView(R.id.port)
  lateinit var portField: EditText

  @BindView(R.id.ssl_enabled)
  lateinit var sslEnabled: SwitchCompat

  private val identityAdapter = IdentityAdapter()
  private val networkAdapter = NetworkAdapter(R.string.settings_chatlist_network_create)

  override fun isValid(): Boolean {
    return (this.network.selectedItemPosition != -1 &&
            networkAdapter.getItemId(this.network.selectedItemPosition) != -1L) ||
           ((this.identity.selectedItemPosition != -1 &&
             identityAdapter.getItemId(this.identity.selectedItemPosition) != -1L) &&
            (nameValidator.isValid && hostValidator.isValid && portValidator.isValid))
  }

  override val title = R.string.slide_user_network_title
  override val description = R.string.slide_user_network_description

  private var data: Bundle? = null
  private var networks: List<INetwork.NetworkInfo>? = null

  override fun setData(data: Bundle) {
    this.data = data
    update()
  }

  override fun getData(data: Bundle) {
    data.putInt(
      "identity",
      identityAdapter.getItemId(this.identity.selectedItemPosition).toInt()
    )
    val networkId = (network.selectedItem as? INetwork.NetworkInfo)?.networkId
    if (networkId != null) {
      data.putInt("network_id", networkId.id)
    } else {
      data.putSerializable(
        "network",
        LinkNetwork(
          name = nameField.text.toString(),
          server = DefaultNetworkServer(
            host = hostField.text.toString(),
            port = portField.text.toString().toUIntOrNull()
                   ?: if (sslEnabled.isChecked) PORT_SSL.port
                   else PORT_PLAINTEXT.port,
            secure = sslEnabled.isChecked
          )
        )
      )
      data.putInt("network_id", -1)
    }
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_network_network, container, false)
    ButterKnife.bind(this, view)
    nameValidator = object : TextValidator(
      requireActivity(), nameWrapper::setError, resources.getString(R.string.hint_invalid_name)
    ) {
      override fun validate(text: Editable) = text.isNotBlank()

      override fun onChanged() = updateValidity()
    }
    hostValidator = object : TextValidator(
      requireActivity(), hostWrapper::setError, resources.getString(R.string.hint_invalid_host)
    ) {
      override fun validate(text: Editable) =
        text.toString().matches(Patterns.DOMAIN_NAME)

      override fun onChanged() = updateValidity()
    }
    portValidator = object : TextValidator(
      requireActivity(), portWrapper::setError, resources.getString(R.string.hint_invalid_port)
    ) {
      override fun validate(text: Editable) = text.toString().toIntOrNull() in (0 until 65536)

      override fun onChanged() = updateValidity()
    }

    nameField.addTextChangedListener(nameValidator)
    hostField.addTextChangedListener(hostValidator)
    portField.addTextChangedListener(portValidator)
    nameValidator.afterTextChanged(nameField.text)
    hostValidator.afterTextChanged(hostField.text)
    portValidator.afterTextChanged(portField.text)

    network.adapter = networkAdapter
    network.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      fun selected(item: INetwork.NetworkInfo?) {
        if (item == null) {
          AnimationHelper.expand(networkGroup)
        } else {
          AnimationHelper.collapse(networkGroup)
        }
        updateValidity()
      }

      override fun onNothingSelected(parent: AdapterView<*>?) = selected(null)

      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) =
        selected(networkAdapter.getItem(position))
    }

    identity.adapter = identityAdapter

    viewModel.identities.switchMap {
      combineLatest(it.values.map(Identity::liveUpdates)).map {
        it.sortedBy(Identity::identityName)
      }
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        identityAdapter.submitList(it)
      }
    })

    viewModel.networks.switchMap {
      combineLatest(it.values.map(Network::liveNetworkInfo)).map {
        it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, INetwork.NetworkInfo::networkName))
      }
    }.toLiveData().observe(this, Observer {
      if (it != null) {
        this.networks = it
        update()
      }
    })

    identity.adapter = identityAdapter

    sslEnabled.setOnCheckedChangeListener { _, isChecked ->
      val portValue = portField.text.trim().toString()
      if (isChecked && portValue == PORT_PLAINTEXT.port.toString()) {
        portField.setText(PORT_SSL.port.toString())
      } else if (!isChecked && portValue == PORT_SSL.port.toString()) {
        portField.setText(PORT_PLAINTEXT.port.toString())
      }
    }

    return view
  }

  private var hasSetUi = false
  private var hasSetNetwork = false

  private fun update() {
    val data = this.data
    val networks = this.networks

    if (data != null && networks != null) {
      networkAdapter.submitList(listOf(null) + networks)
      val linkNetwork = data.getSerializable("network") as? LinkNetwork

      val selectedNetworkId = if (data.containsKey("network_id")) {
        NetworkId(data.getInt("network_id"))
      } else {
        val existingNetwork = networks.firstOrNull {
          it.serverList.any {
            it.host == linkNetwork?.server?.host
          }
        }
        existingNetwork?.networkId
      }
      val selectedNetworkPosition = networkAdapter.indexOf(selectedNetworkId ?: NetworkId(-1)) ?: -1

      if (!hasSetNetwork) {
        if (selectedNetworkPosition != -1 || selectedNetworkId?.isValidId() != true) {
          network.setSelection(selectedNetworkPosition)
          hasSetNetwork = true
        }
      }

      if (!hasSetUi) {
        if (linkNetwork != null && !hasSetUi) {
          nameField.setText(linkNetwork.name)
          hostField.setText(linkNetwork.server.host)
          portField.setText("${linkNetwork.server.port}")
          sslEnabled.isChecked = linkNetwork.server.secure
        }

        if (data.containsKey("identity")) {
          val identity = IdentityId(data.getInt("identity", -1))
          if (identity.isValidId()) {
            val position = identityAdapter.indexOf(identity)
            if (position == -1) {
              this.identity.setSelection(-1)
            }
          }
        }
      }

      updateValidity()
    }
  }

  private lateinit var nameValidator: TextValidator
  private lateinit var hostValidator: TextValidator
  private lateinit var portValidator: TextValidator
}
