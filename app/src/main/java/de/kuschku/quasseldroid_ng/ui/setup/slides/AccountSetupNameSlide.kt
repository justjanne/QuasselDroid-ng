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

class AccountSetupNameSlide : SlideFragment() {
  @BindView(R.id.name)
  lateinit var nameField: TextInputEditText

  private val textWatcher = object : TextWatcher {
    override fun afterTextChanged(p0: Editable?) = updateValidity()
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
  }

  override fun isValid(): Boolean {
    return validName()
  }

  override val title = R.string.slideAccountNameTitle
  override val descripion = R.string.slideAccountNameDescription

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
    nameField.addTextChangedListener(textWatcher)
    return view
  }

  override fun onDestroyView() {
    nameField.removeTextChangedListener(textWatcher)
    super.onDestroyView()
  }

  private fun validName() = nameField.text.isNotEmpty()
}
