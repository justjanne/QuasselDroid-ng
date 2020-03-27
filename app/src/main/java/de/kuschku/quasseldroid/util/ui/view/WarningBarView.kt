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

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.use

class WarningBarView : FrameLayout {
  @BindView(R.id.icon)
  lateinit var icon: AppCompatImageView

  @BindView(R.id.progress)
  lateinit var progress: View

  @BindView(R.id.text)
  lateinit var text: TextView

  constructor(context: Context) :
    this(context, null)

  constructor(context: Context, attrs: AttributeSet?) :
    this(context, attrs, 0)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr) {

    LayoutInflater.from(context).inflate(R.layout.widget_warning_bar, this, true)
    ButterKnife.bind(this)

    context.theme.obtainStyledAttributes(attrs, R.styleable.WarningBarView, 0, 0).use {
      if (it.hasValue(R.styleable.WarningBarView_icon))
        icon.setImageResource(it.getResourceId(R.styleable.WarningBarView_icon, 0))

      if (it.hasValue(R.styleable.WarningBarView_text))
        text.text = it.getString(R.styleable.WarningBarView_text)

      if (it.hasValue(R.styleable.WarningBarView_mode))
        setMode(it.getInt(R.styleable.WarningBarView_mode,
                          MODE_NONE))
    }
  }

  fun setText(content: String) {
    text.text = content
  }

  fun setText(@StringRes content: Int) {
    text.setText(content)
  }

  @SuppressLint("SwitchIntDef")
  fun setMode(@WarningMode mode: Int) {
    when (mode) {
      MODE_NONE     -> {
        visibility = View.GONE
      }
      MODE_TEXT     -> {
        visibility = View.VISIBLE
        icon.visibility = View.GONE
        progress.visibility = View.GONE
      }
      MODE_ICON     -> {
        visibility = View.VISIBLE
        icon.visibility = View.VISIBLE
        progress.visibility = View.GONE
      }
      MODE_PROGRESS -> {
        visibility = View.VISIBLE
        icon.visibility = View.GONE
        progress.visibility = View.VISIBLE
      }
    }
  }

  @IntDef(value = [MODE_NONE, MODE_TEXT, MODE_ICON, MODE_PROGRESS])
  annotation class WarningMode

  companion object {
    const val MODE_NONE = 0
    const val MODE_TEXT = 1
    const val MODE_ICON = 2
    const val MODE_PROGRESS = 3
  }
}
