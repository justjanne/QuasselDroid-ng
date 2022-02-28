package de.justjanne.quasseldroid.ui.theme.quassel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalActivityColors = compositionLocalOf { ActivityColors.Default }

@Immutable
data class ActivityColors(
  val activity: Color,
  val message: Color,
  val highlight: Color,
  val notification: Color,
) {
  companion object {
    val Default = ActivityColors(
      activity = Color(0xffafb42b),
      message = Color(0xff1976d2),
      highlight = Color(0xffffab00),
      notification = Color(0xffd32f2f),
    )
  }
}
