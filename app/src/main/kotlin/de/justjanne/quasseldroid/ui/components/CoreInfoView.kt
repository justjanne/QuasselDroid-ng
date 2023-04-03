package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
//import de.charlex.compose.HtmlText
import de.justjanne.libquassel.protocol.models.ConnectedClient
import de.justjanne.libquassel.protocol.syncables.state.CoreInfoState
import de.justjanne.quasseldroid.sample.SampleCoreInfoProvider
import de.justjanne.quasseldroid.ui.Constants
import de.justjanne.quasseldroid.ui.theme.Typography
import org.threeten.bp.ZoneId

@Preview(name = "Core Info", showBackground = true)
@Composable
fun CoreInfoView(
  @PreviewParameter(SampleCoreInfoProvider::class)
  coreInfo: CoreInfoState
) {
  Column(modifier = Modifier.padding(8.dp)) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(
        text = coreInfo.version,
        style = Typography.body1
      )
      Text(
        text = coreInfo.versionDate
          ?.atZone(ZoneId.systemDefault())
          ?.format(Constants.dateTimeFormatter)
          ?: "Unknown",
        style = Typography.body2
      )
      Text(
        coreInfo.startTime
          .atZone(ZoneId.systemDefault())
          .format(Constants.dateTimeFormatter),
        style = Typography.body2
      )
    }

    LazyColumn {
      items(coreInfo.connectedClients, key = ConnectedClient::id) {
        ConnectedClientCard(
          it,
          modifier = Modifier.padding(8.dp)
        )
      }
    }
  }
}
