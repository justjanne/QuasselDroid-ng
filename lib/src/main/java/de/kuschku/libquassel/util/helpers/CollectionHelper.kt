package de.kuschku.libquassel.util.helpers


/**
 * Returns the sum of all elements in the collection.
 */
@kotlin.jvm.JvmName("sumOfUByte")
fun Iterable<UByte>.sum(): UInt {
  var sum: UInt = 0u
  for (element in this) {
    sum += element
  }
  return sum
}

/**
 * Returns the sum of all elements in the collection.
 */
@kotlin.jvm.JvmName("sumOfUShort")
fun Iterable<UShort>.sum(): UInt {
  var sum: UInt = 0u
  for (element in this) {
    sum += element
  }
  return sum
}

/**
 * Returns the sum of all elements in the collection.
 */
@kotlin.jvm.JvmName("sumOfUInt")
fun Iterable<UInt>.sum(): UInt {
  var sum: UInt = 0u
  for (element in this) {
    sum += element
  }
  return sum
}

/**
 * Returns the sum of all elements in the collection.
 */
@kotlin.jvm.JvmName("sumOfULong")
fun Iterable<ULong>.sum(): ULong {
  var sum: ULong = 0uL
  for (element in this) {
    sum += element
  }
  return sum
}
