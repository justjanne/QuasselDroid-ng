package de.justjanne.quasseldroid.util.format

import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import de.justjanne.libquassel.irc.IrcFormat
import de.justjanne.quasseldroid.ui.theme.QuasselTheme

object IrcFormatRenderer {
  @Composable
  fun render(
    data: Sequence<IrcFormat.Span>,
    // Use default color of Text composable if none set
    textColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    // Use default color of surface if none set
    backgroundColor: Color = MaterialTheme.colors.surface
  ) = buildAnnotatedString {
    for (block in data) {
      pushStyle(buildSpan(block.style, textColor, backgroundColor))
      append(block.content)
      pop()
    }
  }

  @Composable
  private fun toColor(color: IrcFormat.Color?): Color? = when (color) {
    null -> null
    is IrcFormat.Color.Mirc -> QuasselTheme.mirc.colors[color.index]
    is IrcFormat.Color.Hex -> Color(color.color).copy(alpha = 1.0f)
  }

  @Composable
  private fun buildSpan(
    style: IrcFormat.Style,
    textColor: Color,
    backgroundColor: Color
  ): SpanStyle {
    val foreground = toColor(style.foreground) ?: textColor
    val background = toColor(style.background) ?: backgroundColor

    return SpanStyle(
      fontWeight = if (style.flags.contains(IrcFormat.Flag.BOLD)) FontWeight.Bold else FontWeight.Normal,
      fontStyle = if (style.flags.contains(IrcFormat.Flag.ITALIC)) FontStyle.Italic else FontStyle.Normal,
      textDecoration = TextDecoration.combine(
        listOfNotNull(
          if (style.flags.contains(IrcFormat.Flag.STRIKETHROUGH)) TextDecoration.LineThrough else null,
          if (style.flags.contains(IrcFormat.Flag.UNDERLINE)) TextDecoration.Underline else null
        )
      ),
      fontFamily = if (style.flags.contains(IrcFormat.Flag.MONOSPACE)) FontFamily.Monospace else null,
      color = if (style.flags.contains(IrcFormat.Flag.INVERSE)) background else foreground,
      background = if (style.flags.contains(IrcFormat.Flag.INVERSE)) foreground else background,
    )
  }
}
