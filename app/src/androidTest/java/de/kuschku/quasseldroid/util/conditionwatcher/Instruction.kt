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

package de.kuschku.quasseldroid.util.conditionwatcher

import androidx.test.platform.app.InstrumentationRegistry
import de.kuschku.quasseldroid.QuasseldroidAndroidTest

class Instruction(
  val description: String,
  private val condition: InstructionContext.() -> Boolean
) {
  fun checkCondition(): Boolean = condition.invoke(InstructionContext)

  object InstructionContext {
    val application
      get() = (InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as QuasseldroidAndroidTest)

    val activity
      get() = application.activityLifecycleHandler.currentActivity
  }
}
