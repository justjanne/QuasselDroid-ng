/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.util.matcher

import android.view.View
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import de.kuschku.quasseldroid.util.matches
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class IsFullyRenderedMatcher @RemoteMsgConstructor internal constructor(
  areaPercentage: Int
) : TypeSafeMatcher<View>() {

  private val isDisplayingAtLeastMatcher = ViewMatchers.isDisplayingAtLeast(
    areaPercentage)
  private val withEffectiveVisibilityMatcher = ViewMatchers.withEffectiveVisibility(
    ViewMatchers.Visibility.VISIBLE)

  override fun describeTo(description: Description) {
    description.appendText("view is fully rendered")
  }

  public override fun matchesSafely(view: View) = view.matches(
    isDisplayingAtLeastMatcher,
    withEffectiveVisibilityMatcher
  )

  companion object {
    fun isFullyRendered(areaPercentage: Int = 100) = IsFullyRenderedMatcher(
      areaPercentage)
  }
}
