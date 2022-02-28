package de.justjanne.quasseldroid.util.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import de.justjanne.libquassel.protocol.models.ids.BufferId

object BufferIdSaver : Saver<BufferId, Int> {
  override fun restore(value: Int) = BufferId(value)
  override fun SaverScope.save(value: BufferId) = value.id
}
