package de.justjanne.quasseldroid.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NoEncryption
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import de.justjanne.quasseldroid.model.SecurityLevel
import de.justjanne.quasseldroid.sample.SampleSecurityLevelProvider
import de.justjanne.quasseldroid.ui.theme.QuasselTheme

@Preview(name = "Security Icon", showBackground = true)
@Composable
fun SecurityIcon(
  @PreviewParameter(SampleSecurityLevelProvider::class)
  level: SecurityLevel
) {
  val vector = when (level) {
    SecurityLevel.SECURE -> Icons.Filled.Lock
    SecurityLevel.UNVERIFIED -> Icons.Filled.LockOpen
    SecurityLevel.INSECURE -> Icons.Filled.NoEncryption
  }

  val color = when (level) {
    SecurityLevel.SECURE -> QuasselTheme.security.secure
    SecurityLevel.UNVERIFIED -> QuasselTheme.security.unverified
    SecurityLevel.INSECURE -> QuasselTheme.security.insecure
  }

  Icon(imageVector = vector, contentDescription = level.name, tint = color)
}
