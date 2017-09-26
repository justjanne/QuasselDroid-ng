package de.kuschku.libquassel.util.helpers

import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer

/**
 * Because Android’s String::split is broken
 *
 * @return A list with all substrings of length 1, in order
 */
fun String.split() = Array(length) { this.substring(it, it + 1) }

fun String?.serializeString(serializer: StringSerializer) = if (this == null) {
  null
} else {
  serializer.serialize(this)
}
