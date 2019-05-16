/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.ui.setup.user

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
import de.kuschku.quasseldroid.util.Patterns
import de.kuschku.quasseldroid.util.TextValidator
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import javax.inject.Inject

class UserSetupIdentitySlide : SlideFragment() {
  @BindView(R.id.nickWrapper)
  lateinit var nickWrapper: TextInputLayout

  @BindView(R.id.nick)
  lateinit var nickField: EditText

  @BindView(R.id.realnameWrapper)
  lateinit var realnameWrapper: TextInputLayout

  @BindView(R.id.realname)
  lateinit var realnameField: EditText

  @Inject
  lateinit var ircFormatSerializer: IrcFormatSerializer

  @Inject
  lateinit var ircFormatDeserializer: IrcFormatDeserializer

  override fun isValid(): Boolean {
    return nickValidator.isValid
  }

  override val title = R.string.slide_user_identity_title
  override val description = R.string.slide_user_identity_description

  override fun setData(data: Bundle) {
    if (data.containsKey("nick"))
      nickField.setText(data.getString("nick"))
    if (data.containsKey("realname"))
      realnameField.setText(ircFormatDeserializer.formatString(data.getString("realname"), true))
    updateValidity()
  }

  override fun getData(data: Bundle) {
    data.putString("nick", nickField.text.toString())
    data.putString("realname", ircFormatSerializer.toEscapeCodes(realnameField.text))
  }

  override fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_user_identity, container, false)
    ButterKnife.bind(this, view)
    nickValidator = object : TextValidator(
      requireActivity(), nickWrapper::setError, resources.getString(R.string.hint_invalid_nick)
    ) {
      override fun validate(text: Editable) = text.isNotEmpty() && text.matches(Patterns.IRC_NICK)

      override fun onChanged() = updateValidity()
    }

    nickField.addTextChangedListener(nickValidator)
    nickValidator.afterTextChanged(nickField.text)
    return view
  }

  private lateinit var nickValidator: TextValidator
}
