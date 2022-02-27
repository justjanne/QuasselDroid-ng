package de.justjanne.quasseldroid.messages

import de.justjanne.libquassel.protocol.models.Message

data class MessageBuffer(
  /**
   * Whether the chronologically latest message for a given buffer id is in the buffer.
   * If yes, new messages that arrive for this buffer should be appened to the end.
   */
  val atEnd: Boolean,
  val messages: List<Message>
)
