package de.justjanne.quasseldroid.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import de.justjanne.quasseldroid.ui.theme.QuasselTheme
import de.justjanne.quasseldroid.util.irc.SenderColorUtil

@Composable
fun buildNick(nick: String, senderPrefixes: String): AnnotatedString {
  val senderColor = QuasselTheme.sender.colors[SenderColorUtil.senderColor(nick)]

  return buildAnnotatedString {
      if (senderPrefixes.isNotEmpty()) {
          append(senderPrefixes)
      }
      pushStyle(SpanStyle(color = senderColor, fontWeight = FontWeight.Bold))
      append(nick)
      pop()
  }
}
