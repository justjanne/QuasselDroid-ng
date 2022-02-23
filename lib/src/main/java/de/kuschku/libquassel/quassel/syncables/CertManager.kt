/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
    renameObject("${_identityId.id}")
  }

  override fun toVariantMap() = initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetProperties(properties)
  }

  override fun initProperties(): QVariantMap = mapOf(
    "sslKey" to QVariant.of(sslKeyPem(), QtType.QByteArray),
    "sslCert" to QVariant.of(sslCertPem(), QtType.QByteArray)
  )

  override fun initSetProperties(properties: QVariantMap) {
    setSslKey(properties["sslKey"].value(_sslKey))
    setSslCert(properties["sslCert"].value(_sslCert))
  }

  fun sslCertPem() = _sslCert
  fun sslKeyPem() = _sslKey

  override fun setSslCert(encoded: ByteBuffer) {
    _sslCert = encoded
  }

  override fun setSslKey(encoded: ByteBuffer) {
    _sslKey = encoded
  }

  private var _sslKey: ByteBuffer = ByteBuffer.allocate(0)
  private var _sslCert: ByteBuffer = ByteBuffer.allocate(0)
}
