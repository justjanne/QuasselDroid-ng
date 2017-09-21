package de.kuschku.libquassel.util.helpers

/**
 * Because Android’s String::split is broken
 *
 * @return A list with all substrings of length 1, in order
 */
fun String.split(): Array<String> {
  val chars = arrayOfNulls<String>(length)
  val charArray = toCharArray()
  return chars.indices.map { String(charArray, it, 1) }.toTypedArray()
}
