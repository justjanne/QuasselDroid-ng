package de.justjanne.quasseldroid.ui.theme.quassel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalSenderColors = compositionLocalOf { SenderColors.Default }

@Immutable
data class SenderColors(
  val colors: List<Color>,
) {
  companion object {
    val Default = SenderColors(
      colors = listOf(
        Color(0xfff44336),
        Color(0xff2196f3),
        Color(0xff7cb342),
        Color(0xff7b1fa2),
        Color(0xffda8e00),
        Color(0xff4caf50),
        Color(0xff3f51b5),
        Color(0xffe91e63),
        Color(0xffb94600),
        Color(0xff9e9d24),
        Color(0xff558b2f),
        Color(0xff009688),
        Color(0xff0277bd),
        Color(0xff00838f),
        Color(0xff9c27b0),
        Color(0xffc51162),
      )
    )
  }
}
