package de.justjanne.quasseldroid.util.extensions

import de.justjanne.quasseldroid.util.backport.StringJoiner

inline fun joinString(
  delimiter: String = "",
  prefix: String = "",
  suffix: String = "",
  builderAction: StringJoiner.() -> Unit
): String {
  return StringJoiner(delimiter, prefix, suffix).apply(builderAction).toString()
}
