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

class AccountSetupUserSlide : SlideFragment() {
  @BindView(R.id.user)
  lateinit var userField: TextInputEditText

  @BindView(R.id.pass)
  lateinit var passField: TextInputEditText

  private val textWatcher = object : TextWatcher {
    override fun afterTextChanged(p0: Editable?) = updateValidity()
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
  }

  override fun isValid(): Boolean {
    return validUser() && validPass()
  }

  override val title = R.string.slideAccountUserTitle
  override val descripion = R.string.slideAccountUserDescription

  override fun setData(data: Bundle) {
    if (data.containsKey("user"))
      userField.setText(data.getString("user"))
    if (data.containsKey("pass"))
      passField.setText(data.getString("pass"))
  }

  override fun getData(data: Bundle) {
    data.putString("user", userField.text.toString())
    data.putString("pass", passField.text.toString())
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_account_user, container, false)
    ButterKnife.bind(this, view)
    userField.addTextChangedListener(textWatcher)
    passField.addTextChangedListener(textWatcher)
    return view
  }

  override fun onDestroyView() {
    userField.removeTextChangedListener(textWatcher)
    passField.removeTextChangedListener(textWatcher)
    super.onDestroyView()
  }

  private fun validUser() = userField.text.isNotEmpty()
  private fun validPass() = passField.text.isNotEmpty()
}
