package de.justjanne.quasseldroid.util.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import de.justjanne.libquassel.protocol.models.ids.MsgId

object MsgIdSaver : Saver<MsgId, Long> {
  override fun restore(value: Long) = MsgId(value)
  override fun SaverScope.save(value: MsgId) = value.id
}
