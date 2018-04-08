package de.kuschku.quasseldroid.ui.chat.input

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v7.widget.Toolbar
import android.support.v7.widget.TooltipCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R

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

    TooltipCompat.setTooltipText(boldButton, boldButton.contentDescription)
    TooltipCompat.setTooltipText(italicButton, italicButton.contentDescription)
    TooltipCompat.setTooltipText(underlineButton, underlineButton.contentDescription)
    TooltipCompat.setTooltipText(strikethroughButton, strikethroughButton.contentDescription)
    TooltipCompat.setTooltipText(monospaceButton, monospaceButton.contentDescription)
    TooltipCompat.setTooltipText(foregroundButton, foregroundButton.contentDescription)
    TooltipCompat.setTooltipText(backgroundButton, backgroundButton.contentDescription)
    TooltipCompat.setTooltipText(clearButton, clearButton.contentDescription)

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
