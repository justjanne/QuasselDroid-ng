package de.kuschku.quasseldroid_ng.quassel.syncables

import de.kuschku.quasseldroid_ng.protocol.*
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.ICertManager
import de.kuschku.quasseldroid_ng.session.SignalProxy
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
    "sslKey" to QVariant_(sslKeyPem(), Type.QByteArray),
    "sslCert" to QVariant_(sslCertPem(), Type.QByteArray)
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
