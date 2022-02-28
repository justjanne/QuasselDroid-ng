package de.justjanne.quasseldroid.ui.theme.quassel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class UiColors(
  val primary: Color,
  val primaryVariant: Color,
  val secondary: Color,
) {
  companion object {
    val Default = UiColors(
      primary = Color(0xff0a70c0),
      primaryVariant = Color(0xff105a94),
      secondary = Color(0xffffaf3b),
    )
  }
}
