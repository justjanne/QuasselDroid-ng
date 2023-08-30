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

package de.kuschku.quasseldroid.ui.chat.input

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.setTooltip

class RichToolbar : Toolbar {
  lateinit var boldButton: View
  lateinit var italicButton: View
  lateinit var underlineButton: View
  lateinit var strikethroughButton: View
  lateinit var monospaceButton: View
  lateinit var foregroundButton: View
  lateinit var foregroundButtonPreview: View
  lateinit var backgroundButton: View
  lateinit var backgroundButtonPreview: View
  lateinit var clearButton: View

  private var listener: FormattingListener? = null

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    super(context, attrs, defStyleAttr)

  init {
    LayoutInflater.from(context).inflate(R.layout.widget_formatting, this, true)
    this.boldButton = this.findViewById(R.id.action_format_bold)
    this.italicButton = this.findViewById(R.id.action_format_italic)
    this.underlineButton = this.findViewById(R.id.action_format_underline)
    this.strikethroughButton = this.findViewById(R.id.action_format_strikethrough)
    this.monospaceButton = this.findViewById(R.id.action_format_monospace)
    this.foregroundButton = this.findViewById(R.id.action_format_foreground)
    this.foregroundButtonPreview = this.findViewById(R.id.action_format_foreground_preview)
    this.backgroundButton = this.findViewById(R.id.action_format_background)
    this.backgroundButtonPreview = this.findViewById(R.id.action_format_background_preview)
    this.clearButton = this.findViewById(R.id.action_format_clear)

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
