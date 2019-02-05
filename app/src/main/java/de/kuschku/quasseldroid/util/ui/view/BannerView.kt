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

package de.kuschku.quasseldroid.util.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.use

class BannerView : FrameLayout {
  @BindView(R.id.icon)
  lateinit var icon: AppCompatImageView

  @BindView(R.id.text)
  lateinit var text: TextView

  @BindView(R.id.button)
  lateinit var button: TextView

  constructor(context: Context) :
    this(context, null)

  constructor(context: Context, attrs: AttributeSet?) :
    this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {

    LayoutInflater.from(context).inflate(R.layout.widget_banner, this, true)
    ButterKnife.bind(this)

    context.theme.obtainStyledAttributes(attrs, R.styleable.BannerView, 0, 0).use {
      if (it.hasValue(R.styleable.BannerView_icon))
        icon.setImageResource(it.getResourceId(R.styleable.BannerView_icon, 0))

      if (it.hasValue(R.styleable.BannerView_text))
        text.text = it.getString(R.styleable.BannerView_text)

      if (it.hasValue(R.styleable.BannerView_buttonText))
        button.text = it.getString(R.styleable.BannerView_buttonText)
    }
  }

  fun setText(content: String) {
    text.text = content
  }

  fun setText(@StringRes content: Int) {
    text.setText(content)
  }
}
