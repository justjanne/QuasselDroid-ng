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

package de.kuschku.quasseldroid.ui.chat.input

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.setTooltip

class RichToolbar : Toolbar {
  @BindView(R.id.action_format_bold)
  lateinit var boldButton: View

  @BindView(R.id.action_format_italic)
  lateinit var italicButton: View

  @BindView(R.id.action_format_underline)
  lateinit var underlineButton: View

  @BindView(R.id.action_format_strikethrough)
  lateinit var strikethroughButton: View

  @BindView(R.id.action_format_monospace)
  lateinit var monospaceButton: View

  @BindView(R.id.action_format_foreground)
  lateinit var foregroundButton: View

  @BindView(R.id.action_format_foreground_preview)
  lateinit var foregroundButtonPreview: View

  @BindView(R.id.action_format_background)
  lateinit var backgroundButton: View

  @BindView(R.id.action_format_background_preview)
  lateinit var backgroundButtonPreview: View

  @BindView(R.id.action_format_clear)
  lateinit var clearButton: View

  private var listener: FormattingListener? = null

  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr)

  init {
    LayoutInflater.from(context).inflate(R.layout.widget_formatting, this, true)
    ButterKnife.bind(this)

    boldButton.setTooltip()
    italicButton.setTooltip()
    underlineButton.setTooltip()
    strikethroughButton.setTooltip()
    monospaceButton.setTooltip()
    foregroundButton.setTooltip()
    backgroundButton.setTooltip()
    clearButton.setTooltip()

    boldButton.setOnClickListener { listener?.onBold() }
    italicButton.setOnClickListener { listener?.onItalic() }
    underlineButton.setOnClickListener { listener?.onUnderline() }
    strikethroughButton.setOnClickListener { listener?.onStrikethrough() }
    monospaceButton.setOnClickListener { listener?.onMonospace() }
    foregroundButton.setOnClickListener { listener?.onForeground() }
    backgroundButton.setOnClickListener { listener?.onBackground() }
    clearButton.setOnClickListener { listener?.onClear() }
  }

  fun setFormattingListener(listener: FormattingListener?) {
    this.listener = listener
  }

  fun update(
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
    strikethrough: Boolean = false,
    monospace: Boolean = false,
    @ColorInt foreground: Int,
    @ColorInt background: Int
  ) {
    boldButton.isSelected = bold
    italicButton.isSelected = italic
    underlineButton.isSelected = underline
    strikethroughButton.isSelected = strikethrough
    monospaceButton.isSelected = monospace

    foregroundButtonPreview.setBackgroundColor(foreground)
    backgroundButtonPreview.setBackgroundColor(background)
  }

  interface FormattingListener {
    fun onBold() = Unit
    fun onItalic() = Unit
    fun onUnderline() = Unit
    fun onStrikethrough() = Unit
    fun onMonospace() = Unit
    fun onForeground() = Unit
    fun onBackground() = Unit
    fun onClear() = Unit
  }
}
