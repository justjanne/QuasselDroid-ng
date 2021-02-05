/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2021 Janne Mareike Koschinski
 * Copyright (c) 2021 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.protocol.io

import kotlin.concurrent.getOrSet

private val ascii = ThreadLocal<StringEncoder>()
private val utf8 = ThreadLocal<StringEncoder>()
private val utf16 = ThreadLocal<StringEncoder>()

fun stringEncoderAscii() = ascii.getOrSet { StringEncoder(Charsets.ISO_8859_1) }
fun stringEncoderUtf8() = utf8.getOrSet { StringEncoder(Charsets.UTF_8) }
fun stringEncoderUtf16() = utf16.getOrSet { StringEncoder(Charsets.UTF_16BE) }
