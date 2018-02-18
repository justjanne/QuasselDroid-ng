package de.kuschku.quasseldroid_ng.util.irc.format.spans

interface Copyable<out T> {
  fun copy(): T
}
