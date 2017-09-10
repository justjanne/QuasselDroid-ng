package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.ARG
import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.SLOT
import de.kuschku.quasseldroid_ng.protocol.Type
import de.kuschku.quasseldroid_ng.quassel.BufferInfo

@Syncable(name = "AliasManager")
interface IAliasManager : ISyncableObject {
  fun initAliases(): QVariantMap
  fun initSetAliases(aliases: QVariantMap)
  @Slot
  fun addAlias(name: String, expansion: String) {
    SYNC(SLOT, ARG(name, Type.QString), ARG(expansion, Type.QString))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }

  data class Alias(
    val name: String,
    val expansion: String
  )

  data class Command(
    val buffer: BufferInfo,
    val message: String
  )
}
