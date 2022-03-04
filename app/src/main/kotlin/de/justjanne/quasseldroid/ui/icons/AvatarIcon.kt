package de.justjanne.quasseldroid.ui.icons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.justjanne.quasseldroid.sample.SampleNickProvider
import de.justjanne.quasseldroid.ui.theme.QuasselTheme
import de.justjanne.quasseldroid.util.irc.SenderColorUtil
import java.util.*

@Preview
@Composable
fun AvatarIcon(
  @PreviewParameter(SampleNickProvider::class)
  nick: String,
  modifier: Modifier = Modifier,
  //avatar: Bitmap? = null,
  size: Dp = 32.dp
) {
  val senderColor = QuasselTheme.sender.colors[SenderColorUtil.senderColor(nick)]
  val initial = nick.firstOrNull()?.uppercase(Locale.ENGLISH) ?: "?"
  val fontSize = with(LocalDensity.current) { (size.toPx() * 0.67f).toSp() }

  Surface(
    shape = RoundedCornerShape(2.dp),
    color = senderColor,
    modifier = modifier.size(size)
  ) {
    Box {
      Text(
        text = initial,
        color = MaterialTheme.colors.background.copy(alpha = ContentAlpha.medium),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.Center),
        fontSize = fontSize,
      )
    }
  }
}
