/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton,
 * Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.kuschku.quasseldroid.util.helper

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData

/**
 * This function creates a [LiveData] of a [Pair] of the two types provided. The resulting LiveData
 * is updated whenever either input LiveData updates and both LiveData have updated at least once
 * before.
 *
 * If the zip of A and B is C, and A and B are updated in this pattern: `AABA`, C would be updated
 * twice (once with the second A value and first B value, and once with the third A value and first
 * B value).
 *
 * @param a the first LiveData
 * @param b the second LiveData
 * @author Mitchell Skaggs
 */
fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
  return MediatorLiveData<Pair<A, B>>().apply {
    var lastA: A? = null
    var lastB: B? = null

    fun update() {
      val localLastA = lastA
      val localLastB = lastB
      if (localLastA != null && localLastB != null)
        this.value = Pair(localLastA, localLastB)
    }

    addSource(a) {
      lastA = it
      update()
    }
    addSource(b) {
      lastB = it
      update()
    }
  }
}

fun <A, B, C> zipLiveData(a: LiveData<A>, b: LiveData<B>,
                          c: LiveData<C>): LiveData<Triple<A, B, C>> {
  return MediatorLiveData<Triple<A, B, C>>().apply {
    var lastA: A? = null
    var lastB: B? = null
    var lastC: C? = null

    fun update() {
      val localLastA = lastA
      val localLastB = lastB
      val localLastC = lastC
      if (localLastA != null && localLastB != null && localLastC != null)
        this.value = Triple(localLastA, localLastB, localLastC)
    }

    addSource(a) {
      lastA = it
      update()
    }
    addSource(b) {
      lastB = it
      update()
    }
    addSource(c) {
      lastC = it
      update()
    }
  }
}

/**
 * This is merely an extension function for [zipLiveData].
 *
 * @see zipLiveData
 * @author Mitchell Skaggs
 */
fun <A, B> LiveData<A>.zip(b: LiveData<B>): LiveData<Pair<A, B>> =
  zipLiveData(this, b)

/**
 * This is merely an extension function for [zipLiveData].
 *
 * @see zipLiveData
 * @author Mitchell Skaggs
 */
fun <A, B, C> LiveData<A>.zip(b: LiveData<B>,
                              c: LiveData<C>): LiveData<Triple<A, B, C>> =
  zipLiveData(this, b, c)
