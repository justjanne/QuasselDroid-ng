package de.kuschku.justjanne.quasseldroid.util.irc

import androidx.compose.ui.graphics.Color
import de.justjanne.quasseldroid.util.format.IrcFormat
import de.justjanne.quasseldroid.util.format.IrcFormatDeserializer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IrcFormatDeserializerTest {
  @Test
  fun testBroken() {
    assertEquals(
      emptyList(),
      IrcFormatDeserializer.parse(
        "\u000f"
      ).toList()
    )

    assertEquals(
      emptyList(),
      IrcFormatDeserializer.parse(
        "\u0003\u000f"
      ).toList()
    )
    assertEquals(
      listOf(
        IrcFormat.Span(
          "["
        ),
        IrcFormat.Span(
          "hdf-us",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.ITALIC),
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "] ["
        ),
        IrcFormat.Span(
          "nd",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(
          "] blah blah blah"
        ),
      ),
      IrcFormatDeserializer.parse(
        "[\u001d\u000304hdf-us\u0003\u000f] [\u000307nd\u0003] blah blah blah"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "New Break set to: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(2)
          )
        ),
        IrcFormat.Span(
          "Target: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span("388 "),
        IrcFormat.Span(
          "| ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(2)
          )
        ),
        IrcFormat.Span(
          "Type: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span("GS | "),
        IrcFormat.Span(
          "Break: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span("58,000 "),
        IrcFormat.Span(
          "| ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(2)
          )
        ),
        IrcFormat.Span(
          "120%: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span("48,000 | "),
        IrcFormat.Span(
          "135%: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span("43,000 "),
        IrcFormat.Span(
          "| ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(2)
          )
        ),
        IrcFormat.Span(
          "145%: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span("40,000 "),
        IrcFormat.Span(
          "| ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(2)
          )
        ),
        IrcFormat.Span(
          "180%: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span("32,000"),
        IrcFormat.Span(
          " | ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(2)
          )
        ),
        IrcFormat.Span(
          "Pop: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span("73819"),
      ),
      IrcFormatDeserializer.parse(
        "\u000302New Break set to: \u000303Target: \u000399388 \u000302| \u000303Type: " +
          "\u000399GS | \u000303Break: \u00039958,000 \u000302| \u000303120%: \u00039948,000 | " +
          "\u000303135%: \u00039943,000 \u000302| \u000303145%: \u00039940,000 \u000302| " +
          "\u000303180%: \u00039932,000\u000302 | \u000303Pop: \u00039973819\u000f"
      ).toList()
    )
  }

  @Test
  fun testStrikethrough() {
    assertEquals(
      listOf(
        IrcFormat.Span("Normal"),
        IrcFormat.Span(
          "Strikethrough",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.STRIKETHROUGH)
          )
        ),
        IrcFormat.Span("Normal")
      ),
      IrcFormatDeserializer.parse(
        "Normal\u001eStrikethrough\u001eNormal"
      ).toList()
    )
  }

  @Test
  fun testInverse() {
    assertEquals(
      listOf(
        IrcFormat.Span("First"),
        IrcFormat.Span(
          "Second",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.INVERSE)
          )
        ),
        IrcFormat.Span(
          "Red/Green",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.INVERSE),
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "Green/Red",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "Green/Magenta",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(6),
            background = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "Magenta/Green",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.INVERSE),
            foreground = IrcFormat.Color.Mirc(6),
            background = IrcFormat.Color.Mirc(3),
          )
        ),
      ),
      IrcFormatDeserializer.parse(
        "First\u0016Second\u00034,3Red/Green\u0016Green/Red\u00036Green/Magenta\u0016Magenta/Green"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span("First"),
        IrcFormat.Span(
          "Second",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.INVERSE)
          )
        ),
        IrcFormat.Span(
          "Third",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.INVERSE),
            foreground = IrcFormat.Color.Mirc(2)
          )
        ),
        IrcFormat.Span(
          "Red/Green",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "Green/Red",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.INVERSE),
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "Green/Magenta",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.INVERSE),
            foreground = IrcFormat.Color.Mirc(6),
            background = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "Magenta/Green",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(6),
            background = IrcFormat.Color.Mirc(3),
          )
        ),
      ),
      IrcFormatDeserializer.parse(
        "First\u0012Second\u00032Third\u0012\u00034,3Red/Green\u0012Green/Red\u00036Green/Magenta\u0016Magenta/Green"
      ).toList()
    )
  }

  @Test
  fun testMonospace() {
    assertEquals(
      listOf(
        IrcFormat.Span(
          "test ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "test",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.MONOSPACE),
            foreground = IrcFormat.Color.Mirc(4)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u00034test \u0011test"
      ).toList()
    )
    assertEquals(
      listOf(
        IrcFormat.Span(
          "test ",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.MONOSPACE)
          )
        ),
        IrcFormat.Span(
          "test",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.MONOSPACE),
            foreground = IrcFormat.Color.Mirc(4)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u0011test \u00034test"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span("`test "),
        IrcFormat.Span(
          "test`",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "`test \u00034test`"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "[test ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "nick`name",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
            flags = setOf(IrcFormat.Flag.BOLD)
          )
        ),
        IrcFormat.Span(
          "] [nick`name]",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u00034[test \u0002nick`name\u0002] [nick`name]"
      ).toList()
    )
  }

  @Test
  fun testColors() {
    assertEquals(
      listOf(
        IrcFormat.Span("Test 1: "),
        IrcFormat.Span(
          "[",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(
          "6,7,3,9,10,4,8,10,5",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(6)
          )
        ),
        IrcFormat.Span(
          "]",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "Test2: ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(14)
          )
        ),
        IrcFormat.Span(
          " ",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(14)
          )
        ),
        IrcFormat.Span(
          "[",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span("2,9"),
        IrcFormat.Span(
          "]",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
      ),
      IrcFormatDeserializer.parse(
        "Test 1: \u0002\u000312[\u00036\u00026,7,3,9,10,4,8,10,5\u0002\u000312]" +
          "\u0003\u0002 \u000314Test2: \u0002 \u000312[\u0003\u00022,9\u0002\u000312]\u0003\u0002"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "Extended colors",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(55),
            background = IrcFormat.Color.Mirc(25)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000355,25Extended colors\u0003"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "Transparent extended colors",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD, IrcFormat.Flag.UNDERLINE),
            foreground = IrcFormat.Color.Mirc(55),
            background = IrcFormat.Color.Mirc(25)
          )
        ),
        IrcFormat.Span(
          " cleared fg",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD, IrcFormat.Flag.UNDERLINE),
            background = IrcFormat.Color.Mirc(25)
          )
        ),
        IrcFormat.Span(
          " cleared bg",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD, IrcFormat.Flag.UNDERLINE),
            foreground = IrcFormat.Color.Mirc(55)
          )
        ),
        IrcFormat.Span(
          " cleared both",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD, IrcFormat.Flag.UNDERLINE)
          )
        ),
        IrcFormat.Span(
          " cleared bold",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.UNDERLINE)
          )
        ),
        IrcFormat.Span(" cleared all")
      ),
      IrcFormatDeserializer.parse(
        "\u001f\u0002\u000355,25Transparent extended colors\u000399,25 cleared fg\u000355,99 cleared bg\u000399,99 cleared both\u0002 cleared bold\u000f cleared all",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "Sniper_ShooterCZ",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(0),
            background = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "(1)",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(0),
            background = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(":"),
        IrcFormat.Span(
          " kokote",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(2)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u00030,1\u0002Sniper_ShooterCZ\u0002(1)\u000f:\u00032 kokote"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "uncurry",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(9)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "Vect",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(" : "),
        IrcFormat.Span(
          "(Nat,",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "Type)",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(" -> "),
        IrcFormat.Span(
          "Type",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(12)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000309uncurry\u000f \u000312Vect\u000f : \u000312(\u000f\u000312Nat\u000f\u000312," +
          "\u000f \u000312Type\u000f\u000312)\u000f -> \u000312Type\u000f"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span("*** ("),
        IrcFormat.Span(
          "ACTIVITIES",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD)
          )
        ),
        IrcFormat.Span("): Mugging: "),
        IrcFormat.Span(
          "||||||",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3),
            background = IrcFormat.Color.Mirc(3),
          )
        ),
        IrcFormat.Span(
          "||",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(4),
          )
        ),
        IrcFormat.Span(
          "34%",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(0),
            background = IrcFormat.Color.Mirc(4),
          )
        ),
        IrcFormat.Span(
          "||||||||",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(4),
          )
        ),
        IrcFormat.Span(" | [under dev] Piracy: "),
        IrcFormat.Span(
          "|||||||",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(4),
          )
        ),
        IrcFormat.Span(
          "0.9%",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(0),
            background = IrcFormat.Color.Mirc(4),
          )
        ),
        IrcFormat.Span(
          "||||||||",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(4),
          )
        ),
        IrcFormat.Span(" (exploring) | At this rate, you will get: Fined")
      ),
      IrcFormatDeserializer.parse(
        "*** (\u0002ACTIVITIES\u000f): Mugging: \u000303,03|\u000303,03|\u000303,03|\u000303,03|" +
          "\u000303,03|\u000303,03|\u000304,04|\u000304,04|\u000300,043\u000300,044\u000300,04%" +
          "\u000304,04|\u000304,04|\u000304,04|\u000304,04|\u000304,04|\u000304,04|\u000304,04|" +
          "\u000304,04|\u000300,04\u000f | [under dev] Piracy: \u000304,04|\u000304,04|" +
          "\u000304,04|\u000304,04|\u000304,04|\u000304,04|\u000304,04|\u000300,040\u000300,04." +
          "\u000300,049\u000300,04%\u000304,04|\u000304,04|\u000304,04|\u000304,04|\u000304,04|" +
          "\u000304,04|\u000304,04|\u000304,04|\u000300,04\u000f (exploring) | At this rate, you " +
          "will get: Fined",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "\\u000308 ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(8)
          )
        ),
        IrcFormat.Span(
          "\\u000310 ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(10)
          )
        ),
        IrcFormat.Span(
          "\\u0002 ",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(10)
          )
        ),
        IrcFormat.Span(
          "\\u000304 ",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "\\u0002 ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "\\u000309 ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(9)
          )
        ),
        IrcFormat.Span(
          "\\u0002 ",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(9)
          )
        ),
        IrcFormat.Span(
          "\\u0002",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(9)
          )
        ),
      ),
      IrcFormatDeserializer.parse(
        "\u000308\\u000308 \u000310\\u000310 \u0002\\u0002 \u000304\\u000304 \u0002\\u0002 " +
          "\u000309\\u000309 \u0002\\u0002 \u0002\\u0002"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "teal",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(10)
          )
        ),
        IrcFormat.Span(
          "boldteal",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(10)
          )
        ),
        IrcFormat.Span(
          "boldred",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "red",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000310teal\u0002boldteal\u000304boldred\u0002red",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "The channel for help with general IRC things such as ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "clients",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(13)
          )
        ),
        IrcFormat.Span(
          ", ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "BNCs",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(
          ", ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "bots",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          ", ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          "scripting",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(6)
          )
        ),
        IrcFormat.Span(
          " ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(6)
          )
        ),
        IrcFormat.Span(
          "etc.",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u00033The channel for help with general IRC things such as \u0002\u000313clients" +
          "\u0002\u00033, \u0002\u00037BNCs\u0002\u00033, \u0002\u00034bots\u0002\u00033, " +
          "\u0002\u00036scripting\u0002 \u00033etc.",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "hi ",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(10)
          )
        ),
        IrcFormat.Span(
          "hola",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(10)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u0002\u000310hi \u0002hola"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "hi ",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(10)
          )
        ),
        IrcFormat.Span(
          "hola",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000310\u0002hi \u0003hola"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "h",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(10)
          )
        ),
        IrcFormat.Span(
          "i ",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "hola",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u0002\u000310h\u00034i \u0002hola"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "__",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(4),
          )
        ),
        IrcFormat.Span(
          "(",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3),
            background = IrcFormat.Color.Mirc(0),
          )
        ),
        IrcFormat.Span(
          "✰",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(8),
            background = IrcFormat.Color.Mirc(0),
          )
        ),
        IrcFormat.Span(
          ")",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3),
            background = IrcFormat.Color.Mirc(0),
          )
        ),
        IrcFormat.Span(
          "__",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(2),
            background = IrcFormat.Color.Mirc(2),
          )
        ),
        IrcFormat.Span(
          " ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(0),
            background = IrcFormat.Color.Mirc(1),
          )
        ),
        IrcFormat.Span(
          "Ejercito Paraguayo",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(0),
            background = IrcFormat.Color.Mirc(1),
          )
        ),
        IrcFormat.Span(
          " ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(0),
            background = IrcFormat.Color.Mirc(1),
          )
        ),
        IrcFormat.Span(
          "__",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
            background = IrcFormat.Color.Mirc(4),
          )
        ),
        IrcFormat.Span(
          "(",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3),
            background = IrcFormat.Color.Mirc(0),
          )
        ),
        IrcFormat.Span(
          "✰",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(8),
            background = IrcFormat.Color.Mirc(0),
          )
        ),
        IrcFormat.Span(
          ")",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3),
            background = IrcFormat.Color.Mirc(0),
          )
        ),
        IrcFormat.Span(
          "__",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(2),
            background = IrcFormat.Color.Mirc(2),
          )
        ),
        IrcFormat.Span("***** Lord Commander: mdmg - Sub-Comandantes: Sgto_Galleta ***** "),
        IrcFormat.Span(
          " Vencer o Morir!!!  Que alguien pase una nueva xd",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(0),
            background = IrcFormat.Color.Mirc(4),
          )
        ),
        IrcFormat.Span(" http://i.imgur.com/bTWzTuA.jpg"),
      ),
      IrcFormatDeserializer.parse(
        "\u00034,4__\u00033,0(\u00038,0✰\u00033,0)\u00032,2__\u00030,1 \u0002Ejercito Paraguayo" +
          "\u0002 \u00034,4__\u00033,0(\u00038,0✰\u00033,0)\u00032,2__" +
          "\u00031\u0003***** Lord Commander: mdmg - Sub-Comandantes: Sgto_Galleta ***** " +
          "\u00030,4 Vencer o Morir!!!  Que alguien pase una nueva xd" +
          "\u0003 http://i.imgur.com/bTWzTuA.jpg"
      ).toList()
    )

    assertEquals(
      emptyList(),
      IrcFormatDeserializer.parse(
        "\u00034\u000f"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span("hello")
      ),
      IrcFormatDeserializer.parse(
        "\u00034\u000fhello"
      ).toList()
    )

    assertEquals(
      emptyList(),
      IrcFormatDeserializer.parse(
        "\u00031"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          ">bold",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span("test")
      ),
      IrcFormatDeserializer.parse(
        "\u000304\u0002>bold\u0002\u0003test"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "P",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span("layers"),
        IrcFormat.Span(
          "(",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(
          "1/12",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(14)
          )
        ),
        IrcFormat.Span(
          ")",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "Kenzi",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(15)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "C",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span("urrent votewinner: none | ?? help for all the commands | www.no1gaming.eu | #no1"),
      ),
      IrcFormatDeserializer.parse(
        "\u00037P\u000flayers\u00037(\u0003141/12\u00037)\u000f \u000315Kenzi\u0003 \u00037C" +
          "\u000furrent votewinner: none | ?? help for all the commands | www.no1gaming.eu | #no1",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span("First "),
        IrcFormat.Span(
          "Red ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "Green",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(3)
          )
        ),
        IrcFormat.Span(
          " Bold",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "First \u00034Red \u00033Green\u0003\u0002 Bold\u0002\u000f",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span("First "),
        IrcFormat.Span(
          "Color",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          " Bold",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD)
          )
        ),
        IrcFormat.Span(" unnecessary: "),
        IrcFormat.Span(
          "Color",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span("\u0000 plain "),
        IrcFormat.Span(
          "Color",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "Bold",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "No space color New color",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
      ),
      IrcFormatDeserializer.parse(
        "First \u00034Color\u0003\u0002 Bold\u0002 unnecessary:\u0003 \u00034Color" +
          "\u0003\u0000 plain \u00034Color\u0003\u000f \u0002Bold\u000f \u00034No space color" +
          "\u0003\u00034 New color\u000f",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span("DALnet's recommended mIRC scripting & bot help channel. "),
        IrcFormat.Span(
          "Visit us at ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "www.dalnethelpdesk.com",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.UNDERLINE),
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(" for scripting info, forums, and searchable logs/stats "),
        IrcFormat.Span(
          "Looking for a script/bot/addon?",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "mircscripts.org",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD, IrcFormat.Flag.UNDERLINE)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "or",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          "mirc.net",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD, IrcFormat.Flag.UNDERLINE)
          )
        ),
        IrcFormat.Span(" "),
        IrcFormat.Span(
          " Writing your own?",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(
          " Ask ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "here.",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD),
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          " ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(" - "),
        IrcFormat.Span(
          "m",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(
          "IR",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "Casdsaa",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(8)
          )
        ),
        IrcFormat.Span("asdasd v7.14 has been released")
      ),
      IrcFormatDeserializer.parse(
        "DALnet's recommended mIRC scripting & bot help channel. \u00034Visit us at " +
          "\u001fwww.dalnethelpdesk.com\u000f for \u0003scripting info, forums, and searchable " +
          "logs/stats \u000312Looking for a script/bot/addon?\u000f \u0002\u001fmircscripts.org" +
          "\u000f \u00034or\u000f \u0002\u001fmirc.net\u000f \u000312 Writing your own?\u0003" +
          "\u00034 Ask \u0002here.\u0002 \u000f - \u000312m\u00034IR\u00038Casdsaa\u0003asdasd" +
          "\u000f v7.14 has been released",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span("i was last seen \\ \\"),
        IrcFormat.Span(
          "             ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "test^",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(
          "      ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "._",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(
          "   ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          " '--' ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(14)
          )
        ),
        IrcFormat.Span(
          "'-.\\__/ ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(
          "_",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(14)
          )
        ),
        IrcFormat.Span(
          "l",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(12)
          )
        ),
        IrcFormat.Span(
          "       ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "\\",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "        ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "\\",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "      ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "||",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(13)
          )
        ),
        IrcFormat.Span(
          "       ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "/",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(
          "       ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "test",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD), foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "  ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "^",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(
          "    ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          ")",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(9)
          )
        ),
        IrcFormat.Span(
          "\\",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(14)
          )
        ),
        IrcFormat.Span(
          "((((",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(15)
          )
        ),
        IrcFormat.Span(
          "\\",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(
          "   ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          ".",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(7)
          )
        ),
        IrcFormat.Span(
          "            ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          " :;;,,",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(8)
          )
        ),
        IrcFormat.Span(
          "'-._",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "  ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
        IrcFormat.Span(
          "\\",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4)
          )
        ),
        IrcFormat.Span(
          "                        ",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(1)
          )
        ),
      ),
      IrcFormatDeserializer.parse(
        "i was last seen \\ \\\u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 " +
          "\u00031 \u00031 \u00031 \u00031 \u00031 \u00037test^\u00031 \u00031 \u00031 \u00031 " +
          "\u00031 \u00031 \u00037._\u00031 \u00031 \u00031 \u000314 '--' \u000312'-.\\__/ " +
          "\u000314_\u000312l\u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00034\\" +
          "\u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00034\\\u00031 " +
          "\u00031 \u00031 \u00031 \u00031 \u00031 \u000313||\u00031 \u00031 \u00031 \u00031 " +
          "\u00031 \u00031 \u00031 \u00037/\u00031 \u00031 \u00031 \u00031 \u00031 \u00031 " +
          "\u00031 \u00034\u0002test\u0002\u00031 \u00031 \u00037^\u00031 \u00031 \u00031 " +
          "\u00031 \u00039)\u000314\\\u000315((((\u00037\\\u00031 \u00031 \u00031 \u00037." +
          "\u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 " +
          "\u00031 \u00031 \u00038 :;;,,\u00034'-._\u00031 \u00031 \u00034\\\u00031 \u00031 " +
          "\u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 " +
          "\u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 \u00031 " +
          "\u00031 \u00031 \u00031"
      ).toList()
    )
  }

  @Test
  fun testHexColors() {
    assertEquals(
      listOf(
        IrcFormat.Span(
          "some text in 55ee22 rgb",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Hex(Color(0xff55ee22))
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000455ee22some text in 55ee22 rgb\u0004"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          ",some text in 55ee22 rgb",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Hex(Color(0xff55ee22))
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000455ee22,some text in 55ee22 rgb\u0004"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          "some text in 55ee22 rgb on aaaaaa bg",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Hex(Color(0xff55ee22)),
            background = IrcFormat.Color.Hex(Color(0xffaaaaaa))
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000455ee22,aaaaaasome text in 55ee22 rgb on aaaaaa bg\u0004"
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          ",some text in 55ee22 rgb on aaaaaa bg",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Hex(Color(0xff55ee22)),
            background = IrcFormat.Color.Hex(Color(0xffaaaaaa))
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000455ee22,aaaaaa,some text in 55ee22 rgb on aaaaaa bg\u0004",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          ",some text in 55ee22 rgb on aaaaaa bg",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Hex(Color(0xff55ee22)),
            background = IrcFormat.Color.Hex(Color(0xffaaaaaa))
          )
        ),
        IrcFormat.Span(
          " Bold",
          IrcFormat.Style(
            flags = setOf(IrcFormat.Flag.BOLD)
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000455ee22,aaaaaa,some text in 55ee22 rgb on aaaaaa bg\u0004\u0002 Bold\u0002",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          ",some text in 55ee22 rgb on aaaaaa bg",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Hex(Color(0xff55ee22)),
            background = IrcFormat.Color.Hex(Color(0xffaaaaaa))
          )
        ),
        IrcFormat.Span("\u0000")
      ),
      IrcFormatDeserializer.parse(
        "\u000455ee22,aaaaaa,some text in 55ee22 rgb on aaaaaa bg\u0004\u0000",
      ).toList()
    )

    assertEquals(
      listOf(
        IrcFormat.Span(
          ",some text in 55ee22 rgb on aaaaaa bg",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Hex(Color(0xff55ee22)),
            background = IrcFormat.Color.Hex(Color(0xffaaaaaa))
          )
        ),
        IrcFormat.Span(
          " Red",
          IrcFormat.Style(
            foreground = IrcFormat.Color.Mirc(4),
          )
        )
      ),
      IrcFormatDeserializer.parse(
        "\u000455ee22,aaaaaa,some text in 55ee22 rgb on aaaaaa bg\u0004\u00034 Red\u0003",
      ).toList()
    )
  }
}
