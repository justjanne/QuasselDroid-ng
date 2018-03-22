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
import de.kuschku.quasseldroid.util.TextValidator

class AccountSetupNameSlide : SlideFragment() {
  @BindView(R.id.nameWrapper)
  lateinit var nameWrapper: TextInputLayout
  @BindView(R.id.name)
  lateinit var nameField: EditText

  override fun isValid(): Boolean {
    return nameValidator.isValid
  }

  override val title = R.string.slideAccountNameTitle
  override val description = R.string.slideAccountNameDescription

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
      nameWrapper::setError, resources.getString(R.string.hintInvalidName)
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
