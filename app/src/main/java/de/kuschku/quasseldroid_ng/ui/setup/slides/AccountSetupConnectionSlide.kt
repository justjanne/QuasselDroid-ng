package de.kuschku.quasseldroid_ng.ui.setup.slides

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R

class AccountSetupConnectionSlide : SlideFragment() {
  @BindView(R.id.host)
  lateinit var hostField: TextInputEditText

  @BindView(R.id.port)
  lateinit var portField: TextInputEditText

  private val textWatcher = object : TextWatcher {
    override fun afterTextChanged(p0: Editable?) = updateValidity()
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
  }

  override fun isValid(): Boolean {
    return validHost() && validPort()
  }

  override val title = R.string.slideAccountConnectionTitle
  override val descripion = R.string.slideAccountConnectionDescription

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
    hostField.addTextChangedListener(textWatcher)
    portField.addTextChangedListener(textWatcher)
    return view
  }

  override fun onDestroyView() {
    hostField.removeTextChangedListener(textWatcher)
    portField.removeTextChangedListener(textWatcher)
    super.onDestroyView()
  }

  private fun validHost() = hostField.text.isNotEmpty()
  private fun validPort() = (0 until 65536).contains(portField.text.toString().toIntOrNull())
}
