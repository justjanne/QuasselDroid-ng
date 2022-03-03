package de.justjanne.quasseldroid.util.irc

object CRCUtils {
  fun qChecksum(data: ByteArray): Int {
    var crc = 0xffff
    val crcHighBitMask = 0x8000

    for (b in data) {
      val c = reflect(b.toInt(), 8)
      var j = 0x80
      while (j > 0) {
        var highBit = crc and crcHighBitMask
        crc = crc shl 1
        if (c and j > 0) {
          highBit = highBit xor crcHighBitMask
        }
        if (highBit > 0) {
          crc = crc xor 0x1021
        }
        j = j shr 1
      }
    }

    crc = reflect(crc, 16)
    crc = crc xor 0xffff
    crc = crc and 0xffff

    return crc
  }

  private fun reflect(crc: Int, n: Int): Int {
    var j = 1
    var crcout = 0
    var i = 1 shl n - 1
    while (i > 0) {
      if (crc and i > 0) {
        crcout = crcout or j
      }
      j = j shl 1
      i = i shr 1
    }
    return crcout
  }
}
