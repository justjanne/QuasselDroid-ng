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
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import de.kuschku.libquassel.util.helpers.nullIf
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.defaults.DefaultNetwork
import de.kuschku.quasseldroid.defaults.DefaultNetworkServer
import de.kuschku.quasseldroid.ui.setup.SlideFragment
import de.kuschku.quasseldroid.util.Patterns
import de.kuschku.quasseldroid.util.TextValidator
import de.kuschku.quasseldroid.util.ui.AnimationHelper
import javax.inject.Inject

class UserSetupNetworkSlide : SlideFragment() {
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

  @Inject
  lateinit var networkAdapter: DefaultNetworkAdapter

  override fun isValid(): Boolean {
    return (this.network.selectedItemPosition != -1 &&
            networkAdapter.getItem(this.network.selectedItemPosition) != null) ||
           (nameValidator.isValid && hostValidator.isValid && portValidator.isValid)
  }

  override val title = R.string.slide_account_connection_title
  override val description = R.string.slide_account_connection_description

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
            port = portField.text.toString().toIntOrNull()
                   ?: if (sslEnabled.isChecked) 6697
                   else 6667,
            secure = sslEnabled.isChecked
          )
        )
      )
    )
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_user_network, container, false)
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
      fun selected(item: DefaultNetwork?) {
        if (item == null) {
          AnimationHelper.expand(networkGroup)
        } else {
          AnimationHelper.collapse(networkGroup)
        }
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
      if (isChecked && portValue == "6667") {
        portField.setText("6697")
      } else if (!isChecked && portValue == "6697") {
        portField.setText("6667")
      }
    }

    return view
  }

  private lateinit var nameValidator: TextValidator
  private lateinit var hostValidator: TextValidator
  private lateinit var portValidator: TextValidator
}
