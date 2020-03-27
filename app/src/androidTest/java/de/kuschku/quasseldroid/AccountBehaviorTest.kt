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

package de.kuschku.quasseldroid

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity
import de.kuschku.quasseldroid.util.conditionwatcher.ConditionWatcher.waitForCondition
import de.kuschku.quasseldroid.util.conditionwatcher.Instruction
import de.kuschku.quasseldroid.util.matcher.IsFullyRenderedMatcher.Companion.isFullyRendered
import de.kuschku.quasseldroid.util.matches
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep


@RunWith(AndroidJUnit4::class)
@LargeTest
class AccountBehaviorTest {
  @get:Rule
  val activityActivityTestRule = ActivityTestRule(ChatActivity::class.java)

  data class TestData(
    val host: String,
    val port: Int,
    val requireSsl: Boolean,
    val user: String,
    val pass: String
  )

  @Test
  fun actuallyTestThisThing() {
    val testData = TestData(
      host = InstrumentationRegistry.getArguments().getString("host", "localhost"),
      port = InstrumentationRegistry.getArguments().getInt("port", 4242),
      requireSsl = InstrumentationRegistry.getArguments().getBoolean("requireSsl"),
      user = InstrumentationRegistry.getArguments().getString("user", "test"),
      pass = InstrumentationRegistry.getArguments().getString("pass", "test")
    )

    onView(withId(R.id.host))
      .perform(typeText(testData.host))

    onView(withId(R.id.port))
      .perform(
        clearText(),
        typeText(testData.port.toString())
      )

    if (testData.requireSsl) {
      onView(withId(R.id.require_ssl))
        .perform(click())
    }

    waitForCondition(1_000, Instruction("next button should be visible") {
      activity?.findViewById<FloatingActionButton>(R.id.next_button).matches(
        isFullyRendered()
      )
    })

    onView(withId(R.id.next_button))
      .perform(click())

    waitForCondition(1_000, Instruction("user input should be visible") {
      activity?.findViewById<TextInputEditText>(R.id.user).matches(
        isFullyRendered()
      )
    })

    onView(withId(R.id.user))
      .perform(
        clearText(),
        typeText(testData.user)
      )

    onView(withId(R.id.pass))
      .perform(
        clearText(),
        typeText(testData.pass)
      )

    waitForCondition(1_000, Instruction("next button should be visible") {
      activity?.findViewById<FloatingActionButton>(R.id.next_button).matches(
        isFullyRendered()
      )
    })

    onView(withId(R.id.next_button))
      .perform(click())

    waitForCondition(1_000, Instruction("name input should be visible") {
      activity?.findViewById<TextInputEditText>(R.id.name).matches(
        isFullyRendered()
      )
    })

    onView(withId(R.id.name))
      .perform(
        clearText(),
        typeText("Remote Test")
      )

    waitForCondition(1_000, Instruction("next button should be visible") {
      activity?.findViewById<FloatingActionButton>(R.id.next_button).matches(
        isFullyRendered()
      )
    })

    onView(withId(R.id.next_button))
      .perform(click())

    waitForCondition(1_000, Instruction("selection activity should be shown") {
      activity is AccountSelectionActivity
    })

    sleep(500)

    onView(withId(R.id.account_list))
      .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

    waitForCondition(1_000, Instruction("next button should be visible") {
      activity?.findViewById<FloatingActionButton>(R.id.next_button).matches(
        isFullyRendered()
      )
    })

    onView(withId(R.id.next_button))
      .perform(click())

    waitForCondition(1_000, Instruction("chat activity should be shown") {
      activity is ChatActivity
    })

    waitForCondition(10_000, Instruction("connection display should not be shown") {
      activity?.findViewById<View>(R.id.connection_status).matches(
        withEffectiveVisibility(ViewMatchers.Visibility.GONE)
      )
    })
  }
}
