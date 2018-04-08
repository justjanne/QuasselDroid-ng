package de.kuschku.quasseldroid.ui.chat.info

import java.io.Serializable

data class InfoDescriptor(
  val type: InfoType,
  val nick: String? = null,
  val channel: String? = null,
  val buffer: Int,
  val network: Int
) : Serializable
