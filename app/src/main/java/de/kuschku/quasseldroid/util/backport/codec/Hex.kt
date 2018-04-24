/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kuschku.quasseldroid.util.backport.codec

import org.apache.commons.codec.*
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Converts hexadecimal Strings. The charset used for certain operation can be set, the default is set in
 * [.DEFAULT_CHARSET_NAME]
 *
 * This class is thread-safe.
 *
 * @since 1.1
 * @version $Id: Hex.java 1811344 2017-10-06 15:19:57Z ggregory $
 */
class Hex : BinaryEncoder, BinaryDecoder {

  /**
   * Gets the charset.
   *
   * @return the charset.
   * @since 1.7
   */
  val charset: Charset

  /**
   * Gets the charset name.
   *
   * @return the charset name.
   * @since 1.4
   */
  val charsetName: String
    get() = this.charset.name()

  /**
   * Creates a new codec with the default charset name [.DEFAULT_CHARSET]
   */
  constructor() {
    // use default encoding
    this.charset = DEFAULT_CHARSET
  }

  /**
   * Creates a new codec with the given Charset.
   *
   * @param charset
   * the charset.
   * @since 1.7
   */
  constructor(charset: Charset) {
    this.charset = charset
  }

  /**
   * Creates a new codec with the given charset name.
   *
   * @param charsetName
   * the charset name.
   * @throws java.nio.charset.UnsupportedCharsetException
   * If the named charset is unavailable
   * @since 1.4
   * @since 1.7 throws UnsupportedCharsetException if the named charset is unavailable
   */
  constructor(charsetName: String) : this(Charset.forName(charsetName))

  /**
   * Converts an array of character bytes representing hexadecimal values into an array of bytes of those same values.
   * The returned array will be half the length of the passed array, as it takes two characters to represent any given
   * byte. An exception is thrown if the passed char array has an odd number of elements.
   *
   * @param array
   * An array of character bytes containing hexadecimal digits
   * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
   * @throws DecoderException
   * Thrown if an odd number of characters is supplied to this function
   * @see .decodeHex
   */
  @Throws(DecoderException::class)
  override fun decode(array: ByteArray): ByteArray {
    return decodeHex(String(array, charset).toCharArray())
  }

  /**
   * Converts a buffer of character bytes representing hexadecimal values into an array of bytes of those same values.
   * The returned array will be half the length of the passed array, as it takes two characters to represent any given
   * byte. An exception is thrown if the passed char array has an odd number of elements.
   *
   * @param buffer
   * An array of character bytes containing hexadecimal digits
   * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
   * @throws DecoderException
   * Thrown if an odd number of characters is supplied to this function
   * @see .decodeHex
   * @since 1.11
   */
  @Throws(DecoderException::class)
  fun decode(buffer: ByteBuffer): ByteArray {
    return decodeHex(String(buffer.array(), charset).toCharArray())
  }

  /**
   * Converts a String or an array of character bytes representing hexadecimal values into an array of bytes of those
   * same values. The returned array will be half the length of the passed String or array, as it takes two characters
   * to represent any given byte. An exception is thrown if the passed char array has an odd number of elements.
   *
   * @param obj
   * A String, ByteBuffer, byte[], or an array of character bytes containing hexadecimal digits
   * @return A byte array containing binary data decoded from the supplied byte array (representing characters).
   * @throws DecoderException
   * Thrown if an odd number of characters is supplied to this function or the object is not a String or
   * char[]
   * @see .decodeHex
   */
  @Throws(DecoderException::class)
  override fun decode(obj: Any): Any {
    return when (obj) {
      is String     -> decode(obj.toCharArray())
      is ByteArray  -> decode(obj)
      is ByteBuffer -> decode(obj)
      is CharArray  -> decodeHex(obj)
      else          -> throw DecoderException()
    }
  }

  /**
   * Converts an array of bytes into an array of bytes for the characters representing the hexadecimal values of each
   * byte in order. The returned array will be double the length of the passed array, as it takes two characters to
   * represent any given byte.
   *
   *
   * The conversion from hexadecimal characters to the returned bytes is performed with the charset named by
   * [.getCharset].
   *
   *
   * @param array
   * a byte[] to convert to Hex characters
   * @return A byte[] containing the bytes of the lower-case hexadecimal characters
   * @since 1.7 No longer throws IllegalStateException if the charsetName is invalid.
   * @see .encodeHex
   */
  override fun encode(array: ByteArray): ByteArray {
    return encodeHexString(array).toByteArray(this.charset)
  }

  /**
   * Converts byte buffer into an array of bytes for the characters representing the hexadecimal values of each
   * byte in order. The returned array will be double the length of the passed array, as it takes two characters to
   * represent any given byte.
   *
   *
   * The conversion from hexadecimal characters to the returned bytes is performed with the charset named by
   * [.getCharset].
   *
   *
   * @param array
   * a byte buffer to convert to Hex characters
   * @return A byte[] containing the bytes of the lower-case hexadecimal characters
   * @see .encodeHex
   * @since 1.11
   */
  fun encode(array: ByteBuffer): ByteArray {
    return encodeHexString(array).toByteArray(this.charset)
  }

  /**
   * Converts a String or an array of bytes into an array of characters representing the hexadecimal values of each
   * byte in order. The returned array will be double the length of the passed String or array, as it takes two
   * characters to represent any given byte.
   *
   *
   * The conversion from hexadecimal characters to bytes to be encoded to performed with the charset named by
   * [.getCharset].
   *
   *
   * @param obj
   * a String, ByteBuffer, or byte[] to convert to Hex characters
   * @return A char[] containing lower-case hexadecimal characters
   * @throws EncoderException
   * Thrown if the given object is not a String or byte[]
   * @see .encodeHex
   */
  @Throws(EncoderException::class)
  override fun encode(obj: Any) = encodeHex(
    when (obj) {
      is String     -> obj.toByteArray(charset)
      is ByteBuffer -> obj.array()
      is ByteArray  -> obj
      else          -> throw EncoderException()
    }
  )

  /**
   * Returns a string representation of the object, which includes the charset name.
   *
   * @return a string representation of the object.
   */
  override fun toString(): String {
    return super.toString() + "[charsetName=" + this.charset + "]"
  }

  companion object {

    /**
     * Default charset is [Charsets.UTF_8]
     *
     * @since 1.7
     */
    val DEFAULT_CHARSET: Charset = Charsets.UTF_8

    /**
     * Default charset name is [CharEncoding.UTF_8]
     *
     * @since 1.4
     */
    const val DEFAULT_CHARSET_NAME = CharEncoding.UTF_8

    /**
     * Used to build output as Hex
     */
    private val DIGITS_LOWER = charArrayOf('0',
                                           '1',
                                           '2',
                                           '3',
                                           '4',
                                           '5',
                                           '6',
                                           '7',
                                           '8',
                                           '9',
                                           'a',
                                           'b',
                                           'c',
                                           'd',
                                           'e',
                                           'f')

    /**
     * Used to build output as Hex
     */
    private val DIGITS_UPPER = charArrayOf('0',
                                           '1',
                                           '2',
                                           '3',
                                           '4',
                                           '5',
                                           '6',
                                           '7',
                                           '8',
                                           '9',
                                           'A',
                                           'B',
                                           'C',
                                           'D',
                                           'E',
                                           'F')

    /**
     * Converts a String representing hexadecimal values into an array of bytes of those same values. The
     * returned array will be half the length of the passed String, as it takes two characters to represent any given
     * byte. An exception is thrown if the passed String has an odd number of elements.
     *
     * @param data
     * A String containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied char array.
     * @throws DecoderException
     * Thrown if an odd number or illegal of characters is supplied
     * @since 1.11
     */
    @Throws(DecoderException::class)
    fun decodeHex(data: String): ByteArray {
      return decodeHex(data.toCharArray())
    }

    /**
     * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
     * returned array will be half the length of the passed array, as it takes two characters to represent any given
     * byte. An exception is thrown if the passed char array has an odd number of elements.
     *
     * @param data
     * An array of characters containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied char array.
     * @throws DecoderException
     * Thrown if an odd number or illegal of characters is supplied
     */
    @Throws(DecoderException::class)
    fun decodeHex(data: CharArray): ByteArray {

      val len = data.size

      if (len and 0x01 != 0) {
        throw DecoderException("Odd number of characters.")
      }

      val out = ByteArray(len shr 1)

      // two characters form the hex value.
      var i = 0
      var j = 0
      while (j < len) {
        var f = toDigit(data[j], j) shl 4
        j++
        f = f or toDigit(data[j], j)
        j++
        out[i] = (f and 0xFF).toByte()
        i++
      }

      return out
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data
     * a byte[] to convert to Hex characters
     * @param toLowerCase
     * `true` converts to lowercase, `false` to uppercase
     * @return A char[] containing hexadecimal characters in the selected case
     * @since 1.4
     */
    @JvmOverloads
    fun encodeHex(data: ByteArray, toLowerCase: Boolean = true): CharArray {
      return encodeHex(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
    }

    /**
     * Converts a byte buffer into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data
     * a byte buffer to convert to Hex characters
     * @param toLowerCase
     * `true` converts to lowercase, `false` to uppercase
     * @return A char[] containing hexadecimal characters in the selected case
     * @since 1.11
     */
    @JvmOverloads
    fun encodeHex(data: ByteBuffer, toLowerCase: Boolean = true): CharArray {
      return encodeHex(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data
     * a byte[] to convert to Hex characters
     * @param toDigits
     * the output alphabet (must contain at least 16 chars)
     * @return A char[] containing the appropriate characters from the alphabet
     * For best results, this should be either upper- or lower-case hex.
     * @since 1.4
     */
    private fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
      val l = data.size
      val out = CharArray(l shl 1)
      // two characters form the hex value.
      var i = 0
      var j = 0
      while (i < l) {
        out[j++] = toDigits[(0xF0 and data[i].toInt()).ushr(4)]
        out[j++] = toDigits[0x0F and data[i].toInt()]
        i++
      }
      return out
    }

    /**
     * Converts a byte buffer into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data
     * a byte buffer to convert to Hex characters
     * @param toDigits
     * the output alphabet (must be at least 16 characters)
     * @return A char[] containing the appropriate characters from the alphabet
     * For best results, this should be either upper- or lower-case hex.
     * @since 1.11
     */
    private fun encodeHex(data: ByteBuffer, toDigits: CharArray): CharArray {
      return encodeHex(data.array(), toDigits)
    }

    /**
     * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
     * String will be double the length of the passed array, as it takes two characters to represent any given byte.
     *
     * @param data
     * a byte[] to convert to Hex characters
     * @return A String containing lower-case hexadecimal characters
     * @since 1.4
     */
    fun encodeHexString(data: ByteArray): String {
      return String(encodeHex(data))
    }

    /**
     * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
     * String will be double the length of the passed array, as it takes two characters to represent any given byte.
     *
     * @param data
     * a byte[] to convert to Hex characters
     * @param toLowerCase
     * `true` converts to lowercase, `false` to uppercase
     * @return A String containing lower-case hexadecimal characters
     * @since 1.11
     */
    fun encodeHexString(data: ByteArray, toLowerCase: Boolean): String {
      return String(encodeHex(data, toLowerCase))
    }

    /**
     * Converts a byte buffer into a String representing the hexadecimal values of each byte in order. The returned
     * String will be double the length of the passed array, as it takes two characters to represent any given byte.
     *
     * @param data
     * a byte buffer to convert to Hex characters
     * @return A String containing lower-case hexadecimal characters
     * @since 1.11
     */
    fun encodeHexString(data: ByteBuffer): String {
      return String(encodeHex(data))
    }

    /**
     * Converts a byte buffer into a String representing the hexadecimal values of each byte in order. The returned
     * String will be double the length of the passed array, as it takes two characters to represent any given byte.
     *
     * @param data
     * a byte buffer to convert to Hex characters
     * @param toLowerCase
     * `true` converts to lowercase, `false` to uppercase
     * @return A String containing lower-case hexadecimal characters
     * @since 1.11
     */
    fun encodeHexString(data: ByteBuffer, toLowerCase: Boolean): String {
      return String(encodeHex(data, toLowerCase))
    }

    /**
     * Converts a hexadecimal character to an integer.
     *
     * @param ch
     * A character to convert to an integer digit
     * @param index
     * The index of the character in the source
     * @return An integer
     * @throws DecoderException
     * Thrown if ch is an illegal hex character
     */
    @Throws(DecoderException::class)
    private fun toDigit(ch: Char, index: Int): Int {
      val digit = Character.digit(ch, 16)
      if (digit == -1) {
        throw DecoderException("Illegal hexadecimal character $ch at index $index")
      }
      return digit
    }
  }
}
