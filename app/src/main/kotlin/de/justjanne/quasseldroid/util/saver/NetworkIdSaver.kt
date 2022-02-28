package de.justjanne.quasseldroid.util.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import de.justjanne.libquassel.protocol.models.ids.NetworkId

object NetworkIdSaver : Saver<NetworkId, Int> {
  override fun restore(value: Int) = NetworkId(value)
  override fun SaverScope.save(value: NetworkId) = value.id
}
