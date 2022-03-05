package de.justjanne.quasseldroid.util.extensions

import android.view.KeyEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent

fun Modifier.handleTabFocus(focusManager: FocusManager): Modifier =
  onPreviewKeyEvent {
    if (it.key == Key.Tab) {
      if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
        focusManager.moveFocus(
          if (it.nativeKeyEvent.isShiftPressed) FocusDirection.Up
          else FocusDirection.Down
        )
      }
      true
    } else {
      false
    }
  }
