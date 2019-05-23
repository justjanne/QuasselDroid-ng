package de.kuschku.quasseldroid.ui.chat.archive

import de.kuschku.quasseldroid.viewmodel.data.BufferListItem

sealed class ArchiveListItem(val type: Type) {
  data class Header(
    val title: String,
    val content: String
  ) : ArchiveListItem(Type.HEADER)

  data class Placeholder(
    val content: String
  ) : ArchiveListItem(Type.PLACEHOLDER)

  data class Buffer(
    val item: BufferListItem
  ) : ArchiveListItem(Type.BUFFER)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ArchiveListItem) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  enum class Type(val value: UByte) {
    HEADER(0u),
    PLACEHOLDER(1u),
    BUFFER(2u);

    companion object {
      private val map = values().associateBy { it.value }
      fun of(value: UByte) = map[value]
    }
  }
}
