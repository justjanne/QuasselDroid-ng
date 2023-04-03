package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
//import de.charlex.compose.HtmlText
import de.justjanne.libquassel.protocol.models.ConnectedClient
import de.justjanne.quasseldroid.model.SecurityLevel
import de.justjanne.quasseldroid.sample.SampleConnectedClientProvider
import de.justjanne.quasseldroid.ui.Constants
import de.justjanne.quasseldroid.ui.theme.Typography
import org.threeten.bp.ZoneId

@Preview(name = "Connected Client")
@Composable
fun ConnectedClientCard(
  @PreviewParameter(SampleConnectedClientProvider::class)
  client: ConnectedClient,
  modifier: Modifier = Modifier
) {
  Card(modifier = modifier) {
    Row(modifier = Modifier.padding(16.dp)) {
      Column(modifier = Modifier.weight(1.0f)) {
        Text(
          text = client.version,
          style = Typography.body1,
          overflow = TextOverflow.Ellipsis
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
          Text(
            client.remoteAddress,
            style = Typography.body2,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            client.connectedSince
              .atZone(ZoneId.systemDefault())
              .format(Constants.dateTimeFormatter),
            style = Typography.body2
          )
        }
      }
      Spacer(modifier = Modifier.width(16.dp))
      SecurityIcon(level = if (client.secure) SecurityLevel.SECURE else SecurityLevel.INSECURE)
    }
  }
}
