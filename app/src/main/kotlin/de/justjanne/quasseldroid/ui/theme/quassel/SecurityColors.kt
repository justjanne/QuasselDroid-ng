package de.justjanne.quasseldroid.ui.theme.quassel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalSecurityColors = compositionLocalOf { SecurityColors.Default }

@Immutable
data class SecurityColors(
  val secure: Color,
  val unverified: Color,
  val insecure: Color,
) {
  companion object {
    val Default = SecurityColors(
      secure = Color(0xff4caf50),
      unverified = Color(0xffffc107),
      insecure = Color(0xffd32f2f),
    )
  }
}
