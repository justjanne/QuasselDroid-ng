package de.kuschku.libquassel.quassel.syncables.interfaces

import de.kuschku.libquassel.annotations.Slot
import de.kuschku.libquassel.annotations.Syncable
import de.kuschku.libquassel.protocol.ARG
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import java.nio.ByteBuffer

@Syncable(name = "CertManager")
interface ICertManager : ISyncableObject {

  fun initProperties(): QVariantMap
  fun initSetProperties(properties: QVariantMap)

  @Slot
  fun setSslCert(encoded: ByteBuffer?) {
    SYNC("setSslCert", ARG(encoded, Type.QByteArray))
  }

  @Slot
  fun setSslKey(encoded: ByteBuffer?) {
    SYNC("setSslKey", ARG(encoded, Type.QByteArray))
  }

  @Slot
  override fun update(properties: QVariantMap) {
    super.update(properties)
  }
}
