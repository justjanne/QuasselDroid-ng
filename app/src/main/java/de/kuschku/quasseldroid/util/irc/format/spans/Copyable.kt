package de.kuschku.quasseldroid.util.irc.format.spans

interface Copyable<out T> {
  fun copy(): T
}
