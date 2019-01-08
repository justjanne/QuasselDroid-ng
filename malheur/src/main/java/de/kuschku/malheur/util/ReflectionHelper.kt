/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
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

package de.kuschku.malheur.util

fun reflectionCollectConstants(klass: Class<*>?) = klass?.declaredFields
  ?.mapNotNull {
    var result: Pair<String, Any?>? = null
    try {
      result = it.name to it.get(null)
    } catch (e: IllegalAccessException) {
    } catch (e: IllegalArgumentException) {
    }
    result
  }?.toMap()

fun <T> reflectionCollectGetters(klass: Class<T>?) = klass?.declaredMethods
  ?.filter { it.parameterTypes.isEmpty() && it.returnType != Void::class.java }
  ?.filter { it.name != "getClass" }
  ?.filter { it.name.startsWith("get") || it.name.startsWith("is") }
  ?.mapNotNull {
    var result: Pair<String, Any?>? = null
    try {
      result = it.name to it.invoke(it)
    } catch (e: IllegalAccessException) {
    } catch (e: IllegalArgumentException) {
    }
    result
  }?.toMap()
