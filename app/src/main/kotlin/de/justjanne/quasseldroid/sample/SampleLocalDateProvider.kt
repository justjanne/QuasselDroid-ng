package de.justjanne.quasseldroid.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.threeten.bp.LocalDate

class SampleLocalDateProvider : PreviewParameterProvider<LocalDate> {
  override val values = sequenceOf(
    LocalDate.of(2022, 2, 28)
  )
}
