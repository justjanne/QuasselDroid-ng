package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.*
import de.kuschku.libquassel.quassel.syncables.interfaces.ICertManager
import de.kuschku.libquassel.session.SignalProxy
import java.nio.ByteBuffer

class CertManager constructor(
  private val _identityId: IdentityId,
  proxy: SignalProxy
) : SyncableObject(proxy, "CertManager"), ICertManager {
  override fun init() {
    renameObject("$_identityId")
  }

  override fun toVariantMap() = initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetProperties(properties)
  }

  override fun initProperties(): QVariantMap = mapOf(
    "sslKey" to QVariant.of(sslKeyPem(), Type.QByteArray),
    "sslCert" to QVariant.of(sslCertPem(), Type.QByteArray)
  )

  override fun initSetProperties(properties: QVariantMap) {
    setSslKey(properties["sslKey"].value())
    setSslCert(properties["sslCert"].value())
  }

  fun sslCertPem() = _sslCert
  fun sslKeyPem() = _sslKey

  override fun setSslCert(encoded: ByteBuffer?) {
    _sslCert = encoded
    super.setSslCert(encoded)
  }

  override fun setSslKey(encoded: ByteBuffer?) {
    _sslKey = encoded
    super.setSslKey(encoded)
  }

  private var _sslKey: ByteBuffer? = null
  private var _sslCert: ByteBuffer? = null
}
