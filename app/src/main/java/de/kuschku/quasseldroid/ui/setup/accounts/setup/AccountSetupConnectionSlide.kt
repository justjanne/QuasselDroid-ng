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

package de.kuschku.quasseldroid.ui.setup.accounts.setup

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.setup.SlideFragment
import de.kuschku.quasseldroid.util.Patterns
import de.kuschku.quasseldroid.util.TextValidator

class AccountSetupConnectionSlide : SlideFragment() {
  @BindView(R.id.hostWrapper)
  lateinit var hostWrapper: TextInputLayout

  @BindView(R.id.host)
  lateinit var hostField: EditText

  @BindView(R.id.portWrapper)
  lateinit var portWrapper: TextInputLayout

  @BindView(R.id.port)
  lateinit var portField: EditText

  override fun isValid(): Boolean {
    return hostValidator.isValid && portValidator.isValid
  }

  override val title = R.string.slide_account_connection_title
  override val description = R.string.slide_account_connection_description

  override fun setData(data: Bundle) {
    if (data.containsKey("host"))
      hostField.setText(data.getString("host"))
    if (data.containsKey("port"))
      portField.setText(data.getInt("port").toString())
    updateValidity()
  }

  override fun getData(data: Bundle) {
    data.putString("host", hostField.text.toString())
    data.putInt("port", portField.text.toString().toIntOrNull() ?: -1)
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_account_connection, container, false)
    ButterKnife.bind(this, view)
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

    hostField.addTextChangedListener(hostValidator)
    portField.addTextChangedListener(portValidator)
    hostValidator.afterTextChanged(hostField.text)
    portValidator.afterTextChanged(portField.text)
    return view
  }

  private lateinit var hostValidator: TextValidator
  private lateinit var portValidator: TextValidator
}
