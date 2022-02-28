package de.justjanne.quasseldroid.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import de.justjanne.quasseldroid.model.SecurityLevel

class SampleSecurityLevelProvider: PreviewParameterProvider<SecurityLevel> {
  override val values = sequenceOf(
    SecurityLevel.SECURE,
    SecurityLevel.UNVERIFIED,
    SecurityLevel.INSECURE,
  )
}
