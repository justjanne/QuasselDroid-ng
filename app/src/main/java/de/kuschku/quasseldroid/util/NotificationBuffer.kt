package de.kuschku.quasseldroid.util

import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.protocol.Buffer_Types

data class NotificationBuffer(
  val id: BufferId,
  val type: Buffer_Types,
  val name: String,
  val networkName: String
)
