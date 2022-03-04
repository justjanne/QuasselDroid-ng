package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(name = "Message Placeholder", showBackground = true)
@Composable
fun MessagePlaceholder() {
  val density = LocalDensity.current
  fun TextUnit.toDp() = with(density) { toPx().toDp() }

  Row(
    modifier = Modifier
      .padding(2.dp)
      .fillMaxWidth()
  ) {
    Spacer(Modifier.width(4.dp))
    Surface(
      shape = RoundedCornerShape(2.dp),
      color = Color.Gray,
      modifier = Modifier
        .padding(2.dp)
        .size(32.dp)
    ) {}
    Spacer(Modifier.width(4.dp))
    Column(
      modifier = Modifier
        .align(Alignment.CenterVertically)
        .padding(vertical = 2.dp)
    ) {
      Row(
        modifier = Modifier
          .height(12.sp.toDp())
          .fillMaxWidth()
      ) {
        Surface(
          modifier = Modifier
            .width(62.sp.toDp())
            .fillMaxHeight(),
          color = Color.Gray,
        ) {}
        Spacer(modifier = Modifier.width(7.dp))
        Surface(
          modifier = Modifier
            .width(163.sp.toDp())
            .fillMaxHeight(),
          color = Color.LightGray,
        ) {}
      }
      Spacer(modifier = Modifier.height(4.dp))
      Row {
        Column {
          Surface(
            modifier = Modifier
              .width(280.sp.toDp())
              .height(14.sp.toDp()),
            color = Color.Gray,
          ) {}
          Spacer(modifier = Modifier.height(2.dp))
          Surface(
            modifier = Modifier
              .width(160.sp.toDp())
              .height(14.sp.toDp()),
            color = Color.Gray,
          ) {}
        }
        Spacer(modifier = Modifier.weight(1.0f))
        Surface(
          modifier = Modifier
            .size(width = 34.sp.toDp(), height = 12.sp.toDp())
            .padding(end = 2.dp)
            .align(Alignment.Bottom),
          color = Color.LightGray
        ) {}
      }
    }
  }
}
