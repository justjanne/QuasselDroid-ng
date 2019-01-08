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

package de.kuschku.quasseldroid.ui.setup.accounts.setup

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.setup.SlideFragment
import de.kuschku.quasseldroid.util.TextValidator

class AccountSetupNameSlide : SlideFragment() {
  @BindView(R.id.nameWrapper)
  lateinit var nameWrapper: TextInputLayout
  @BindView(R.id.name)
  lateinit var nameField: EditText

  override fun isValid(): Boolean {
    return nameValidator.isValid
  }

  override val title = R.string.slide_account_name_title
  override val description = R.string.slide_account_name_description

  override fun setData(data: Bundle) {
    if (data.containsKey("name"))
      nameField.setText(data.getString("name"))
    updateValidity()
  }

  override fun getData(data: Bundle) {
    data.putString("name", nameField.text.toString())
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_account_name, container, false)
    ButterKnife.bind(this, view)
    nameValidator = object : TextValidator(
      requireActivity(), nameWrapper::setError, resources.getString(R.string.hint_invalid_name)
    ) {
      override fun validate(text: Editable) = text.isNotBlank()

      override fun onChanged() = updateValidity()
    }
    nameField.addTextChangedListener(nameValidator)
    nameValidator.afterTextChanged(nameField.text)
    return view
  }

  private lateinit var nameValidator: TextValidator
}
