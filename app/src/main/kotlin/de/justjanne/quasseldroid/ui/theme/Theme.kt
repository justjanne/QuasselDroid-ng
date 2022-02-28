package de.justjanne.quasseldroid.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import de.justjanne.quasseldroid.ui.theme.quassel.ActivityColors
import de.justjanne.quasseldroid.ui.theme.quassel.ChatColors
import de.justjanne.quasseldroid.ui.theme.quassel.LocalActivityColors
import de.justjanne.quasseldroid.ui.theme.quassel.LocalChatColors
import de.justjanne.quasseldroid.ui.theme.quassel.LocalMircColors
import de.justjanne.quasseldroid.ui.theme.quassel.LocalSecurityColors
import de.justjanne.quasseldroid.ui.theme.quassel.LocalSenderColors
import de.justjanne.quasseldroid.ui.theme.quassel.MircColors
import de.justjanne.quasseldroid.ui.theme.quassel.SecurityColors
import de.justjanne.quasseldroid.ui.theme.quassel.SenderColors
import de.justjanne.quasseldroid.ui.theme.quassel.UiColors

@Composable
fun QuasselTheme(
  dark: Boolean = isSystemInDarkTheme(),
  ui: UiColors = UiColors.Default,
  chat: ChatColors = ChatColors.Default,
  activity: ActivityColors = ActivityColors.Default,
  security: SecurityColors = SecurityColors.Default,
  sender: SenderColors = SenderColors.Default,
  mirc: MircColors = MircColors.Default,
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colors = buildColors(dark, ui),
    typography = Typography,
    shapes = Shapes
  ) {
    CompositionLocalProvider(
      LocalChatColors provides chat,
      LocalActivityColors provides activity,
      LocalSecurityColors provides security,
      LocalSenderColors provides sender,
      LocalMircColors provides mirc,
      content = content
    )
  }
}

private fun buildColors(dark: Boolean, ui: UiColors): Colors = if (dark) {
  darkColors(
    primary = ui.primary,
    primaryVariant = ui.primaryVariant,
    secondary = ui.secondary,
  )
} else {
  lightColors(
    primary = ui.primary,
    primaryVariant = ui.primaryVariant,
    secondary = ui.secondary,
  )
}

object QuasselTheme {
  val chat: ChatColors
    @Composable get() = LocalChatColors.current
  val activity: ActivityColors
    @Composable get() = LocalActivityColors.current
  val security: SecurityColors
    @Composable get() = LocalSecurityColors.current
  val sender: SenderColors
    @Composable get() = LocalSenderColors.current
  val mirc: MircColors
    @Composable get() = LocalMircColors.current
}
