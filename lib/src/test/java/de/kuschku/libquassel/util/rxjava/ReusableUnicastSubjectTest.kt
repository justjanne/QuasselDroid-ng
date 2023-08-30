/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.libquassel.util.rxjava

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReusableUnicastSubjectTest {
  @Test
  fun test() {
    // We have Object A and B1, B2, etc.
    //
    // Object A should provide an observable to Objects B1, B2, etc.
    // Object A will at some points publish items to this Observable.
    // As long as no subscriber is subscribed, these items should be buffered.
    //
    // As soon as a subscriber subscribers, it should get all buffered items, as well as all that
    // come after until it unsubscribes.
    //
    // If the subscriber unsubscribes again, the observable should buffer incoming items again,
    // until another subscriber subscribes again

    val expected1 = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val received1 = mutableListOf<Int>()

    val expected2 = listOf(11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)
    val received2 = mutableListOf<Int>()

    val expected3 = listOf(21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
    val received3 = mutableListOf<Int>()

    // We create our observable, this is supposed to be in Object A
    val subject = ReusableUnicastSubject.create<Int>()
    val observable = subject.publish().refCount()

    // And emit items while no subscriber is subscribed.
    // These should get buffered
    for (i in 1..5) {
      subject.onNext(i)
    }

    // B1 subscribes, the subscriber should now receive all buffered items
    val subscription1 = observable.subscribe {
      received1.add(it)
    }

    // We emit items while a subscriber is subscribed,
    // these shouldn’t get buffered but instead directly consumed by the subscriber
    for (i in 6..10) {
      subject.onNext(i)
    }
    // B1 unsubscribes again, from now on items should get buffered again
    subscription1.dispose()

    // These items should get buffered again
    for (i in 11..15) {
      subject.onNext(i)
    }
    // As soon as B2 subscribes, it should receive the buffered items 11..15
    val subscription2 = observable.subscribe {
      received2.add(it)
    }

    // These items should get directly consumed by the subscriber again
    for (i in 16..20) {
      subject.onNext(i)
    }
    // B3 should receive no buffered items
    val subscription3 = observable.subscribe {
      received3.add(it)
    }
    for (i in 21..25) {
      subject.onNext(i)
    }
    // And B2 unsubscribes again
    subscription2.dispose()
    // These items should get directly consumed by the B3
    for (i in 26..30) {
      subject.onNext(i)
    }
    subscription3.dispose()

    assertEquals(expected1, received1)
    assertEquals(expected2, received2)
    assertEquals(expected3, received3)
  }
}
