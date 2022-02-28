package de.justjanne.quasseldroid.ui.theme.quassel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalMircColors = compositionLocalOf { MircColors.Default }

@Immutable
data class MircColors(
  val colors: List<Color>,
) {
  companion object {
    val Default = MircColors(
      colors = listOf(
        Color(0xffffffff),
        Color(0xff000000),
        Color(0xff000080),
        Color(0xff008000),
        Color(0xffff0000),
        Color(0xff800000),
        Color(0xff800080),
        Color(0xffffa500),
        Color(0xffffff00),
        Color(0xff00ff00),
        Color(0xff008080),
        Color(0xff00ffff),
        Color(0xff4169e1),
        Color(0xffff00ff),
        Color(0xff808080),
        Color(0xffc0c0c0),

        Color(0xff470000),
        Color(0xff740000),
        Color(0xffb50000),
        Color(0xffff0000),
        Color(0xffff5959),
        Color(0xffff9c9c),

        Color(0xff472100),
        Color(0xff743a00),
        Color(0xffb56300),
        Color(0xffff8c00),
        Color(0xffffb459),
        Color(0xffffd39c),

        Color(0xff474700),
        Color(0xff747400),
        Color(0xffb5b500),
        Color(0xffffff00),
        Color(0xffffff71),
        Color(0xffffff9c),

        Color(0xff324700),
        Color(0xff517400),
        Color(0xff7db500),
        Color(0xffb2ff00),
        Color(0xffcfff60),
        Color(0xffe2ff9c),

        Color(0xff004700),
        Color(0xff007400),
        Color(0xff00b500),
        Color(0xff00ff00),
        Color(0xff6fff6f),
        Color(0xff9cff9c),

        Color(0xff00472c),
        Color(0xff007449),
        Color(0xff00b571),
        Color(0xff00ffa0),
        Color(0xff65ffc9),
        Color(0xff9cffdb),

        Color(0xff004747),
        Color(0xff007474),
        Color(0xff00b5b5),
        Color(0xff00ffff),
        Color(0xff6dffff),
        Color(0xff9cffff),

        Color(0xff002747),
        Color(0xff004074),
        Color(0xff0063b5),
        Color(0xff008cff),
        Color(0xff59b4ff),
        Color(0xff9cd3ff),

        Color(0xff000047),
        Color(0xff000074),
        Color(0xff0000b5),
        Color(0xff0000ff),
        Color(0xff5959ff),
        Color(0xff9c9cff),

        Color(0xff2e0047),
        Color(0xff4b0074),
        Color(0xff7500b5),
        Color(0xffa500ff),
        Color(0xffc459ff),
        Color(0xffdc9cff),

        Color(0xff470047),
        Color(0xff740074),
        Color(0xffb500b5),
        Color(0xffff00ff),
        Color(0xffff66ff),
        Color(0xffff9cff),

        Color(0xff47002a),
        Color(0xff740045),
        Color(0xffb5006b),
        Color(0xffff0098),
        Color(0xffff59bc),
        Color(0xffff94d3),

        Color(0xff000000),
        Color(0xff131313),
        Color(0xff282828),
        Color(0xff363636),
        Color(0xff4d4d4d),
        Color(0xff656565),
        Color(0xff818181),
        Color(0xff9f9f9f),
        Color(0xffbcbcbc),
        Color(0xffe2e2e2),
        Color(0xffffffff),
      )
    )
  }
}
