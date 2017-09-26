package de.kuschku.libquassel.protocol

enum class Protocol(private val value: Byte) {
  Legacy(0x01),
  Datastream(0x02);

  fun toDouble(): Double = value.toDouble()
  fun toFloat(): Float = value.toFloat()
  fun toLong(): Long = value.toLong()
  fun toInt(): Int = value.toInt()
  fun toChar(): Char = value.toChar()
  fun toShort(): Short = value.toShort()
  fun toByte(): Byte = value
}
