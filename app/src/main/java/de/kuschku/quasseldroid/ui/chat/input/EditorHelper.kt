/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat.input

import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.v4.app.FragmentActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.util.helper.getColorCompat
import de.kuschku.quasseldroid.util.helper.lastWordIndices
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.ui.ColorChooserDialog
import io.reactivex.subjects.BehaviorSubject

class EditorHelper(
  private val activity: FragmentActivity,
  private val editText: RichEditText,
  private val toolbar: RichToolbar,
  private val autoCompleteHelper: AutoCompleteHelper,
  autoCompleteSettings: AutoCompleteSettings,
  appearanceSettings: AppearanceSettings
) {
  private var enterListener: (() -> Unit)? = null

  private val mircColors = listOf(
    R.color.mircColor00, R.color.mircColor01, R.color.mircColor02, R.color.mircColor03,
    R.color.mircColor04, R.color.mircColor05, R.color.mircColor06, R.color.mircColor07,
    R.color.mircColor08, R.color.mircColor09, R.color.mircColor10, R.color.mircColor11,
    R.color.mircColor12, R.color.mircColor13, R.color.mircColor14, R.color.mircColor15
  ).map(activity::getColorCompat).toIntArray()

  private val defaultForegroundColor = editText.context.theme.styledAttributes(R.attr.colorForeground) {
    getColor(0, 0)
  }

  private val defaultBackgroundColor = editText.context.theme.styledAttributes(R.attr.colorBackground) {
    getColor(0, 0)
  }

  val lastWord: BehaviorSubject<Pair<String, IntRange>> =
    BehaviorSubject.createDefault(Pair("", IntRange.EMPTY))
  private val textWatcher = object : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
      val previous = autoCompleteHelper.autoCompletionState
      val next = if (previous != null && s != null) {
        val suffix = if (previous.range.start == 0) previous.completion.suffix else " "
        val sequence = if (s.length < previous.range.start) ""
        else s.substring(previous.range.start)
        if (sequence == previous.completion.name + suffix) {
          previous.originalWord to (previous.range.start until s.length)
        } else {
          autoCompleteHelper.autoCompletionState = null
          s.lastWordIndices(editText.selectionStart, onlyBeforeCursor = true)?.let { indices ->
            s.substring(indices) to indices
          }
        }
      } else {
        s?.lastWordIndices(editText.selectionStart, onlyBeforeCursor = true)?.let { indices ->
          s.substring(indices) to indices
        }
      }

      lastWord.onNext(next ?: Pair("", IntRange.EMPTY))
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
  }

  init {
    toolbar.setFormattingListener(object : RichToolbar.FormattingListener {
      override fun onBold() = editText.toggleBold()
      override fun onItalic() = editText.toggleItalic()
      override fun onUnderline() = editText.toggleUnderline()
      override fun onStrikethrough() = editText.toggleStrikethrough()
      override fun onMonospace() = editText.toggleMonospace()
      override fun onForeground() = showColorChooser(R.string.label_foreground,
                                                     editText.foregroundColor(),
                                                     defaultForegroundColor) {
        editText.toggleForeground(color = it)
      }

      override fun onBackground() = showColorChooser(R.string.label_background,
                                                     editText.backgroundColor(),
                                                     defaultBackgroundColor) {
        editText.toggleBackground(color = it)
      }

      override fun onClear() = editText.clearFormatting()
    })
    autoCompleteHelper.setAutocompleteListener(editText::autoComplete)
    editText.setFormattingListener { bold, italic, underline, strikethrough, monospace, foreground, background ->
      toolbar.update(
        bold,
        italic,
        underline,
        strikethrough,
        monospace,
        foreground ?: defaultForegroundColor,
        background ?: defaultBackgroundColor
      )
    }
    editText.addTextChangedListener(textWatcher)
    editText.setOnKeyListener { _, keyCode, event: KeyEvent? ->
      if (event?.action == KeyEvent.ACTION_DOWN) {
        if (event.isCtrlPressed && !event.isAltPressed) when (keyCode) {
          KeyEvent.KEYCODE_B -> {
            editText.toggleBold()
            true
          }
          KeyEvent.KEYCODE_I -> {
            editText.toggleItalic()
            true
          }
          KeyEvent.KEYCODE_U -> {
            editText.toggleUnderline()
            true
          }
          else               -> false
        } else when (keyCode) {
          KeyEvent.KEYCODE_ENTER,
          KeyEvent.KEYCODE_NUMPAD_ENTER -> if (event.isShiftPressed) {
            false
          } else {
            enterListener?.invoke()
            true
          }
          KeyEvent.KEYCODE_TAB          -> {
            if (!event.isAltPressed && !event.isCtrlPressed) {
              autoCompleteHelper.autoComplete(event.isShiftPressed)
              true
            } else {
              false
            }
          }
          else                          -> false
        }
      } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
        !(event?.isShiftPressed ?: false)
      } else {
        false
      }
    }

    if (autoCompleteSettings.doubleTap) {
      editText.setDoubleClickListener {
        autoCompleteHelper.autoComplete()
      }
    }

    editText.imeOptions = when (appearanceSettings.inputEnter) {
      AppearanceSettings.InputEnterMode.EMOJI -> listOf(
        EditorInfo.IME_ACTION_NONE,
        EditorInfo.IME_FLAG_NO_EXTRACT_UI
      )
      AppearanceSettings.InputEnterMode.SEND  -> listOf(
        EditorInfo.IME_ACTION_SEND,
        EditorInfo.IME_FLAG_NO_EXTRACT_UI
      )
    }.fold(0, Int::or)
  }

  fun setOnEnterListener(listener: (() -> Unit)?) {
    this.enterListener = listener
  }

  fun setMultiLine(enabled: Boolean) = editText.setMultiLine(enabled)

  fun replaceText(text: CharSequence?) = editText.replaceText(text)

  fun appendText(text: CharSequence?, suffix: String?) = editText.appendText(text, suffix)

  private fun showColorChooser(
    @StringRes title: Int,
    @ColorInt preselect: Int?,
    @ColorInt default: Int,
    f: (Int?) -> Unit
  ) {
    var selectedColor: Int? = preselect
    ColorChooserDialog.Builder(editText.context, title)
      .customColors(mircColors, null)
      .doneButton(R.string.label_select)
      .cancelButton(R.string.label_reset)
      .backButton(R.string.label_back)
      .customButton(R.string.label_colors_custom)
      .presetsButton(R.string.label_colors_mirc)
      .preselect(preselect ?: default)
      .dynamicButtonColor(false)
      .allowUserColorInputAlpha(false)
      .callback(object : ColorChooserDialog.ColorCallback {
        override fun onColorReset(dialog: ColorChooserDialog) {
          selectedColor = null
        }

        override fun onColorSelection(dialog: ColorChooserDialog, color: Int) {
          selectedColor = color
        }

        override fun onColorChooserDismissed(dialog: ColorChooserDialog) {
          f(selectedColor)
        }
      })
      .show(activity)
  }
}
