/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.util.helpers

import de.kuschku.libquassel.protocol.primitive.serializer.StringSerializer

/**
 * Because Android’s String::split is broken
 *
 * @return A list with all substrings of length 1, in order
 */
fun String.split() = Array(length) { this.substring(it, it + 1) }

fun String?.serializeString(serializer: StringSerializer) = if (this == null) {
  null
} else {
  serializer.serialize(this)
}
