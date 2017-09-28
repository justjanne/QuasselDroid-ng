package de.kuschku.quasseldroid_ng.ui.setup.accounts

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.setup.SlideFragment
import de.kuschku.quasseldroid_ng.util.Patterns
import de.kuschku.quasseldroid_ng.util.TextValidator

class AccountSetupConnectionSlide : SlideFragment() {
  @BindView(R.id.hostWrapper)
  lateinit var hostWrapper: TextInputLayout
  @BindView(R.id.host)
  lateinit var hostField: TextInputEditText

  @BindView(R.id.portWrapper)
  lateinit var portWrapper: TextInputLayout

  @BindView(R.id.port)
  lateinit var portField: TextInputEditText

  override fun isValid(): Boolean {
    return hostValidator.isValid && portValidator.isValid
  }

  override val title = R.string.slideAccountConnectionTitle
  override val description = R.string.slideAccountConnectionDescription

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
      hostWrapper::setError, resources.getString(R.string.hintInvalidHost)
    ) {
      override fun validate(text: Editable)
        = text.toString().matches(Patterns.DOMAIN_NAME.toRegex())

      override fun onChanged() = updateValidity()
    }
    portValidator = object : TextValidator(
      portWrapper::setError, resources.getString(R.string.hintInvalidPort)
    ) {
      override fun validate(text: Editable)
        = text.toString().toIntOrNull() in (0 until 65536)

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
