package de.justjanne.quasseldroid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import de.charlex.compose.HtmlText
import de.justjanne.libquassel.protocol.models.ConnectedClient
import de.justjanne.quasseldroid.R
import de.justjanne.quasseldroid.sample.SampleConnectedClientProvider
import de.justjanne.quasseldroid.ui.theme.Insecure
import de.justjanne.quasseldroid.ui.theme.Secure
import de.justjanne.quasseldroid.ui.theme.Typography
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

private val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

@Preview(name = "Connected Client")
@Composable
fun ConnectedClientCard(
  @PreviewParameter(SampleConnectedClientProvider::class)
  client: ConnectedClient,
  modifier: Modifier = Modifier
) {
  val secureResource = painterResource(
    if (client.secure) R.drawable.ic_lock
    else R.drawable.ic_no_encryption
  )

  val tint = if (client.secure) Secure else Insecure

  Card(modifier = modifier) {
    Row(modifier = Modifier.padding(16.dp)) {
      Column(modifier = Modifier.weight(1.0f)) {
        HtmlText(
          text = client.version,
          style = Typography.body1
        )
        Text(
          client.remoteAddress,
          style = Typography.body2
        )
        Text(
          client.connectedSince
            .atZone(ZoneId.systemDefault())
            .format(formatter),
          style = Typography.body2
        )
      }
      Spacer(modifier = Modifier.width(16.dp))
      Icon(secureResource, tint = tint, contentDescription = "")
    }
  }
}
