package de.justjanne.quasseldroid.ui.theme.quassel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalChatColors = compositionLocalOf { ChatColors.Default }

@Immutable
data class ChatColors(
  val action: Color,
  val onAction: Color,
  val notice: Color,
  val onNotice: Color,
  val highlight: Color,
  val onHighlight: Color,
  val error: Color,
  val onError: Color,
) {
  companion object {
    val Default = ChatColors(
      action = Color(0x00000000),
      onAction = Color(0xff01579b),
      notice = Color(0x00000000),
      onNotice = Color(0xffb56a00),
      highlight = Color(0x40ffaf3b),
      onHighlight = Color(0xde000000),
      error = Color(0x00000000),
      onError = Color(0xffb71c1c),
    )
  }
}
