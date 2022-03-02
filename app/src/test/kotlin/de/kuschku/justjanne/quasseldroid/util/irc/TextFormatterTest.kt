package de.kuschku.justjanne.quasseldroid.util.irc

import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.text.buildSpannedString
import de.justjanne.quasseldroid.util.format.TextFormatter
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class TextFormatterTest {
  @Test
  fun testBasicFormatting() {
    val calendar = GregorianCalendar(1995, Calendar.MAY, 23, 13, 34, 18)

    assertEquals(
      " d  c  b  a",
      TextFormatter.format(
        "%4\$2s %3\$2s %2\$2s %1\$2s", "a", "b", "c", "d",
        locale = Locale.ENGLISH
      )
    )
    assertEquals(
      "e =    +2,7183",
      TextFormatter.format("e = %+10.4f", Math.E, locale = Locale.FRANCE)
    )
    assertEquals(
      "Amount gained or lost since last statement: $ (6,217.58)",
      TextFormatter.format(
        "Amount gained or lost since last statement: \$ %(,.2f", -6217.58,
        locale = Locale.ENGLISH
      )
    )
    assertEquals(
      "Local time: 13:34:18",
      TextFormatter.format(
        "Local time: %tT",
        calendar,
        locale = Locale.ENGLISH
      )
    )
    assertEquals(
      "Unable to open file 'food': No such file or directory",
      TextFormatter.format(
        "Unable to open file '%1\$s': %2\$s",
        "food",
        "No such file or directory",
        locale = Locale.ENGLISH
      )
    )
    assertEquals(
      "Duke's Birthday: May 23, 1995",
      TextFormatter.format(
        "Duke's Birthday: %1\$tb %1\$te, %1\$tY",
        calendar,
        locale = Locale.ENGLISH
      )
    )
    assertEquals(
      "Duke's Birthday: May 23, 1995",
      TextFormatter.format(
        "Duke's Birthday: %1\$tb %<te, %<tY",
        calendar,
        locale = Locale.ENGLISH,
      )
    )
  }

  @Test
  fun testAnnotatedStrings() {
    assertEquals(
      buildAnnotatedString {
        append("Hello ")
        pushStyle(SpanStyle(color = Color.Red))
        append("World")
        pop()
        append(", ")
        pushStyle(SpanStyle(color = Color.Blue))
        append("I love you")
        pop()
        append('!')
      },
      TextFormatter.format(
        buildAnnotatedString {
          append("Hello %1\$s, ")
          pushStyle(SpanStyle(color = Color.Blue))
          append("I love you")
          pop()
          append('!')
        },
        buildAnnotatedString {
          pushStyle(SpanStyle(color = Color.Red))
          append("World")
          pop()
        }
      )
    )
  }
}
