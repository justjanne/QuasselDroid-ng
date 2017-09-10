package de.kuschku.quasseldroid_ng.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.quasseldroid_ng.protocol.ARG
import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.SLOT
import de.kuschku.quasseldroid_ng.protocol.Type
import java.nio.ByteBuffer

@Syncable(name = "CertManager")
interface ICertManager : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun setSslCert(encoded: ByteBuffer?) {
    SYNC(SLOT, ARG(encoded, Type.QByteArray))
  }

  @Slot
  fun setSslKey(encoded: ByteBuffer?) {
    SYNC(SLOT, ARG(encoded, Type.QByteArray))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
