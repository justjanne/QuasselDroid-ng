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
import de.kuschku.libquassel.quassel.QuasselFeatures
import de.kuschku.libquassel.quassel.syncables.interfaces.ICoreInfo
import de.kuschku.libquassel.session.SignalProxy
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.Instant

class CoreInfo constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "CoreInfo"), ICoreInfo {
  override fun toVariantMap() = initProperties()

  override fun fromVariantMap(properties: QVariantMap) {
    initSetProperties(properties)
  }

  override fun initProperties(): QVariantMap = mapOf(
    "coreData" to QVariant.of(coreData(), Type.QVariantMap)
  )

  override fun initSetProperties(properties: QVariantMap) {
    setCoreData(properties["coreData"].value(coreData()))
  }

  override fun setCoreData(data: QVariantMap) {
    _coreData = data
    super.setCoreData(data)
  }

  fun coreData() = _coreData
  fun info() = _coreData.let {
    CoreData(
      quasselVersion = it["quasselVersion"]?.value("") ?: "",
      quasselBuildDate = it["quasselBuildDate"]?.value("") ?: "",
      startTime = it["startTime"]?.value(Instant.EPOCH) ?: Instant.EPOCH,
      sessionConnectedClients = it["sessionConnectedClients"]?.value(0) ?: 0,
      sessionConnectedClientData = it["sessionConnectedClientData"]?.value(emptyList<QVariant_>())?.map {
        it.value(emptyMap<String, QVariant_>()).let {
          ConnectedClientData(
            id = it["id"]?.value(0) ?: 0,
            remoteAddress = it["remoteAddress"]?.value("") ?: "",
            location = it["location"]?.value("") ?: "",
            clientVersion = it["clientVersion"]?.value("") ?: "",
            clientVersionDate = it["clientVersionDate"]?.value("") ?: "",
            connectedSince = it["connectedSince"]?.value(Instant.EPOCH) ?: Instant.EPOCH,
            secure = it["secure"]?.value(false) ?: false,
            features = QuasselFeatures(
              Legacy_Feature.of(it["features"]?.value(0) ?: 0),
              it["featureList"]?.valueOr(::emptyList) ?: emptyList()
            )
          )
        }
      } ?: emptyList()
    )
  }

  fun liveInfo() = live_coreData.map { info() }

  private val live_coreData = BehaviorSubject.createDefault(Unit)
  private var _coreData: QVariantMap = emptyMap()
    set(value) {
      field = value
      live_coreData.onNext(Unit)
    }

  data class CoreData(
    val quasselVersion: String,
    val quasselBuildDate: String,
    val startTime: Instant,
    val sessionConnectedClients: Int,
    val sessionConnectedClientData: List<ConnectedClientData>
  )

  data class ConnectedClientData(
    val id: Int,
    val remoteAddress: String,
    val location: String,
    val clientVersion: String,
    val clientVersionDate: String,
    val connectedSince: Instant,
    val secure: Boolean,
    val features: QuasselFeatures
  )
}
