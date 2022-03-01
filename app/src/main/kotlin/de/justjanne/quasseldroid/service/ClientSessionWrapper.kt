package de.justjanne.quasseldroid.service

import de.justjanne.libquassel.client.session.ClientSession
import de.justjanne.quasseldroid.messages.MessageStore

data class ClientSessionWrapper(
  val session: ClientSession,
  val messages: MessageStore
)
