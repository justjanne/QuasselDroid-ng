package de.justjanne.quasseldroid.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class SampleBooleanProvider : PreviewParameterProvider<Boolean> {
  override val values = sequenceOf(false, true)
}
