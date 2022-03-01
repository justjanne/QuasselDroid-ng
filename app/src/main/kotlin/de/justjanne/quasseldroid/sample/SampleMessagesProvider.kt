package de.justjanne.quasseldroid.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.justjanne.libquassel.protocol.models.Message

class SampleMessagesProvider : PreviewParameterProvider<List<Message>> {
  override val values = sequenceOf(
    SampleMessageProvider().values.toList()
  )
}
