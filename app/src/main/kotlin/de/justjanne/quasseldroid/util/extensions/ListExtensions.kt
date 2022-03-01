package de.justjanne.quasseldroid.util.extensions

fun <T> List<T>.getSafe(index: Int): T? =
  if (index !in 0..size) null
  else get(index)

fun <T> List<T>.getPrevious(index: Int): T? =
  getSafe(index - 1)

fun <T> List<T>.getNext(index: Int): T? =
  getSafe(index + 1)
