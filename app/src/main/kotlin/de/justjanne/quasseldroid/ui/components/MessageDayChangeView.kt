package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.justjanne.quasseldroid.ui.Constants
import de.justjanne.quasseldroid.ui.theme.QuasselTheme
import de.justjanne.quasseldroid.ui.theme.Typography
import org.threeten.bp.LocalDate

@Preview(name = "Day Change", showBackground = true)
@Composable
private fun MessageDayChangePreview() {
  Column {
    MessageDayChangeView(LocalDate.of(2018, 9, 7), isNew = false)
    MessageDayChangeView(LocalDate.of(2018, 9, 7), isNew = true)
  }
}

@Composable
fun MessageDayChangeView(
  date: LocalDate,
  isNew: Boolean
) {
  val foregroundColor =
    if (isNew) QuasselTheme.security.insecure
    else MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)

  Row(modifier = Modifier.padding(vertical = 4.dp)) {
    Spacer(Modifier.width(8.dp))
    Divider(
      color = foregroundColor,
      modifier = Modifier
        .weight(1.0f)
        .height(1.dp)
        .align(Alignment.CenterVertically)
    )
    Spacer(Modifier.width(4.dp))
    Text(
      date.format(Constants.dateFormatter),
      modifier = Modifier
        .align(Alignment.CenterVertically),
      style = Typography.body2,
      fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.width(4.dp))
    Row(
      modifier = Modifier
        .weight(1.0f)
        .align(Alignment.CenterVertically)
    ) {
      Divider(
        color = foregroundColor,
        modifier = Modifier
          .weight(1.0f)
          .height(1.dp)
          .align(Alignment.CenterVertically)
      )
      if (isNew) {
        Spacer(Modifier.width(4.dp))
        Text(
          "New",
          modifier = Modifier
            .align(Alignment.CenterVertically),
          color = QuasselTheme.security.insecure,
          style = Typography.body2,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
        )
      }
    }
    Spacer(Modifier.width(8.dp))
  }
}

