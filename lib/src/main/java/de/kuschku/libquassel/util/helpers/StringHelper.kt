package de.kuschku.libquassel.util.helpers

/**
 * Because Android’s String::split is broken
 *
 * @return A list with all substrings of length 1, in order
 */
fun String.split() = Array(length) { this.substring(it, it + 1) }
