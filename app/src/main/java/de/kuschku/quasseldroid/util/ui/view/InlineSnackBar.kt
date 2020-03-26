/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.quasseldroid.util.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.use

class InlineSnackBar : FrameLayout {
  @BindView(R.id.text)
  lateinit var text: TextView

  @BindView(R.id.button)
  lateinit var button: Button

  constructor(context: Context) :
    this(context, null)

  constructor(context: Context, attrs: AttributeSet?) :
    this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {

    LayoutInflater.from(context).inflate(R.layout.widget_inline_snackbar, this, true)
    ButterKnife.bind(this)

    context.theme.obtainStyledAttributes(attrs, R.styleable.InlineSnackBar, 0, 0).use {
      if (it.hasValue(R.styleable.InlineSnackBar_text))
        text.text = it.getString(R.styleable.InlineSnackBar_text)

      if (it.hasValue(R.styleable.InlineSnackBar_buttonText))
        button.text = it.getString(R.styleable.InlineSnackBar_buttonText)
    }
  }

  fun setText(content: String) {
    text.text = content
  }

  fun setText(@StringRes content: Int) {
    text.setText(content)
  }

  fun setButtonText(content: String) {
    button.text = content
  }

  fun setButtonText(@StringRes content: Int) {
    button.setText(content)
  }

  fun setOnClickListener(listener: ((View) -> Unit)?) {
    button.setOnClickListener(listener)
  }

  override fun setOnClickListener(listener: OnClickListener?) {
    button.setOnClickListener(listener)
  }
}
