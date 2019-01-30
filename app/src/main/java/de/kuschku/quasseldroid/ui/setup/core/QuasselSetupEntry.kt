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

package de.kuschku.quasseldroid.ui.setup.core

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.kuschku.libquassel.protocol.QVariant_
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.coresetup.CoreSetupBackendConfigElement
import de.kuschku.libquassel.protocol.value
import de.kuschku.quasseldroid.R

class QuasselSetupEntry : FrameLayout {
  @BindView(R.id.wrapper)
  lateinit var wrapper: TextInputLayout

  @BindView(R.id.field)
  lateinit var field: TextInputEditText

  private var data: CoreSetupBackendConfigElement? = null

  constructor(context: Context) :
    this(context, null)

  constructor(context: Context, attrs: AttributeSet?) :
    this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    this(context, attrs, defStyleAttr, null)

  constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
              data: CoreSetupBackendConfigElement? = null
  ) : super(context, attrs, defStyleAttr) {

    LayoutInflater.from(context).inflate(R.layout.widget_quassel_setup_entry, this, true)
    ButterKnife.bind(this)

    if (data != null) {
      this.data = data

      wrapper.hint = data.displayName
      when {
        data.defaultValue.type == Type.QString &&
        data.key.contains("password", ignoreCase = true) -> {
          wrapper.isPasswordVisibilityToggleEnabled = true
          field.inputType =
            InputType.TYPE_CLASS_TEXT or
              InputType.TYPE_TEXT_VARIATION_PASSWORD
          field.setText(data.defaultValue.value(""))
        }
        data.defaultValue.type == Type.QString &&
        data.key.contains("hostname", ignoreCase = true) -> {
          field.inputType =
            InputType.TYPE_CLASS_TEXT or
              InputType.TYPE_TEXT_VARIATION_URI
          field.setText(data.defaultValue.value(""))
        }
        data.defaultValue.type == Type.QString           -> {
          field.inputType =
            InputType.TYPE_CLASS_TEXT or
              InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or
              InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
          field.setText(data.defaultValue.value(""))
        }
        data.defaultValue.type == Type.Int               -> {
          field.inputType =
            InputType.TYPE_CLASS_NUMBER
          field.setText(data.defaultValue.value<Int>()?.toString())
        }
      }
    }
  }

  fun key() = data?.key ?: ""

  fun value(): QVariant_ {
    val rawValue = field.text.toString()
    val data = this.data

    return when (data?.defaultValue?.type) {
      Type.QString -> {
        QVariant_.of(rawValue, data.defaultValue.type)
      }
      Type.Int     -> {
        QVariant_.of(rawValue.toInt(), data.defaultValue.type)
      }
      else         -> {
        QVariant_.of("", Type.QString)
      }
    }
  }
}
