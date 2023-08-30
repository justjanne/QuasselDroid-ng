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

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.textfield.TextInputLayout
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork.PortDefaults.PORT_PLAINTEXT
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork.PortDefaults.PORT_SSL
import de.kuschku.libquassel.util.helper.nullIf
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.defaults.DefaultNetwork
import de.kuschku.quasseldroid.defaults.DefaultNetworkServer
import de.kuschku.quasseldroid.ui.setup.SlideFragment
import de.kuschku.quasseldroid.util.Patterns
import de.kuschku.quasseldroid.util.TextValidator
import de.kuschku.quasseldroid.util.ui.AnimationHelper
import javax.inject.Inject

class UserSetupNetworkSlide : SlideFragment() {
  lateinit var network: Spinner
  lateinit var networkGroup: ViewGroup
  lateinit var nameWrapper: TextInputLayout
  lateinit var nameField: EditText
  lateinit var hostWrapper: TextInputLayout
  lateinit var hostField: EditText
  lateinit var portWrapper: TextInputLayout
  lateinit var portField: EditText
  lateinit var sslEnabled: SwitchCompat

  @Inject
  lateinit var networkAdapter: DefaultNetworkAdapter

  override fun isValid(): Boolean {
    return (this.network.selectedItemPosition != -1 &&
            networkAdapter.getItem(this.network.selectedItemPosition) != null) ||
           (nameValidator.isValid && hostValidator.isValid && portValidator.isValid)
  }

  override val title = R.string.slide_user_network_title
  override val description = R.string.slide_user_network_description

  override fun setData(data: Bundle) {
    if (data.containsKey("network")) {
      val network = data.getSerializable("network") as? DefaultNetwork
                    ?: networkAdapter.default()
      if (network != null) {
        val position = networkAdapter.indexOf(network)
        if (position == -1) {
          this.network.setSelection(networkAdapter.indexOf(null))
          network.servers.firstOrNull()?.let {
            this.hostField.setText(it.host)
            this.portField.setText(it.port.toString())
            this.sslEnabled.isChecked = it.secure
          }
        }
      }
    }
    updateValidity()
  }

  override fun getData(data: Bundle) {
    data.putSerializable(
      "network",
      networkAdapter.getItem(this.network.selectedItemPosition)
      ?: DefaultNetwork(
        name = nameField.text.toString(),
        servers = listOf(
          DefaultNetworkServer(
            host = hostField.text.toString(),
            port = portField.text.toString().toUIntOrNull()
                   ?: if (sslEnabled.isChecked) PORT_SSL.port
                   else PORT_PLAINTEXT.port,
            secure = sslEnabled.isChecked
          )
        )
      )
    )
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_user_network, container, false)
    this.network = view.findViewById(R.id.network)
    this.networkGroup = view.findViewById(R.id.network_group)
    this.nameWrapper = view.findViewById(R.id.nameWrapper)
    this.nameField = view.findViewById(R.id.name)
    this.hostWrapper = view.findViewById(R.id.hostWrapper)
    this.hostField = view.findViewById(R.id.host)
    this.portWrapper = view.findViewById(R.id.portWrapper)
    this.portField = view.findViewById(R.id.port)
    this.sslEnabled = view.findViewById(R.id.ssl_enabled)
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
      fun selected(item: DefaultNetwork?) {
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
    networkAdapter.default()?.let {
      networkAdapter.indexOf(it).nullIf { it == -1 }?.let {
        network.setSelection(it)
      }
    }

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

  private lateinit var nameValidator: TextValidator
  private lateinit var hostValidator: TextValidator
  private lateinit var portValidator: TextValidator
}
