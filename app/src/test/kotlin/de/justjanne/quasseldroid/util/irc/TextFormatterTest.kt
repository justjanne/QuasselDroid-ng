package de.justjanne.quasseldroid.util.irc

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import de.justjanne.quasseldroid.util.extensions.format
import de.justjanne.quasseldroid.util.format.FormatString
import de.justjanne.quasseldroid.util.format.TextFormatter
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class TextFormatterTest {
  private val calendar = GregorianCalendar(1995, Calendar.MAY, 23, 13, 34, 18)

  @Test
  fun testNumberedArgumentIndex() {
    assertEquals(
      listOf(
        FormatString.FormatSpecifier(argumentIndex = 4, width = 2, conversion = 's'),
        FormatString.FixedValue(" "),
        FormatString.FormatSpecifier(argumentIndex = 3, width = 2, conversion = 's'),
        FormatString.FixedValue(" "),
        FormatString.FormatSpecifier(argumentIndex = 2, width = 2, conversion = 's'),
        FormatString.FixedValue(" "),
        FormatString.FormatSpecifier(argumentIndex = 1, width = 2, conversion = 's')
      ),
      TextFormatter.parseBlocks("%4\$2s %3\$2s %2\$2s %1\$2s").toList()
    )
    assertEquals(
      " d  c  b  a",
      TextFormatter.format(
        "%4\$2s %3\$2s %2\$2s %1\$2s", "a", "b", "c", "d",
        locale = Locale.ENGLISH
      )
    )
  }

  @Test
  fun testReuseFlag() {
    assertEquals(
      listOf(
        FormatString.FixedValue("Duke's Birthday: "),
        FormatString.FormatSpecifier(argumentIndex = 1, time = true, conversion = 'b'),
        FormatString.FixedValue(" "),
        FormatString.FormatSpecifier(flags = "<", time = true, conversion = 'e'),
        FormatString.FixedValue(", "),
        FormatString.FormatSpecifier(flags = "<", time = true, conversion = 'Y'),
      ),
      TextFormatter.parseBlocks("Duke's Birthday: %1\$tb %<te, %<tY").toList()
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
  fun testFloatFormatting() {
    assertEquals(
      listOf(
        FormatString.FixedValue("e = "),
        FormatString.FormatSpecifier(flags = "+", width = 10, precision = 4, conversion = 'f'),
      ),
      TextFormatter.parseBlocks("e = %+10.4f").toList()
    )
    assertEquals(
      "e =    +2,7183",
      TextFormatter.format("e = %+10.4f", Math.E, locale = Locale.FRANCE)
    )
  }

  @Test
  fun testAccountingFormatting() {
    assertEquals(
      listOf(
        FormatString.FixedValue("Amount gained or lost since last statement: \$ "),
        FormatString.FormatSpecifier(flags = "(,", precision = 2, conversion = 'f'),
      ),
      TextFormatter.parseBlocks("Amount gained or lost since last statement: \$ %(,.2f").toList()
    )
    assertEquals(
      "Amount gained or lost since last statement: $ (6,217.58)",
      TextFormatter.format(
        "Amount gained or lost since last statement: \$ %(,.2f", -6217.58,
        locale = Locale.ENGLISH
      )
    )
  }

  @Test
  fun testDateTimeFormatting() {
    assertEquals(
      listOf(
        FormatString.FixedValue("Local time: "),
        FormatString.FormatSpecifier(time = true, conversion = 'T'),
      ),
      TextFormatter.parseBlocks("Local time: %tT").toList()
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
      listOf(
        FormatString.FixedValue("Duke's Birthday: "),
        FormatString.FormatSpecifier(argumentIndex = 1, time = true, conversion = 'b'),
        FormatString.FixedValue(" "),
        FormatString.FormatSpecifier(argumentIndex = 1, time = true, conversion = 'e'),
        FormatString.FixedValue(", "),
        FormatString.FormatSpecifier(argumentIndex = 1, time = true, conversion = 'Y'),
      ),
      TextFormatter.parseBlocks("Duke's Birthday: %1\$tb %1\$te, %1\$tY").toList()
    )
    assertEquals(
      "Duke's Birthday: May 23, 1995",
      TextFormatter.format(
        "Duke's Birthday: %1\$tb %1\$te, %1\$tY",
        calendar,
        locale = Locale.ENGLISH
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
