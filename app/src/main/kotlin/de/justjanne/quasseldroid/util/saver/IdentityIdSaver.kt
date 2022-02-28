package de.justjanne.quasseldroid.util.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import de.justjanne.libquassel.protocol.models.ids.IdentityId

object IdentityIdSaver : Saver<IdentityId, Int> {
  override fun restore(value: Int) = IdentityId(value)
  override fun SaverScope.save(value: IdentityId) = value.id
}
