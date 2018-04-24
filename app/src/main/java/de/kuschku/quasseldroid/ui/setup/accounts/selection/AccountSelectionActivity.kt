/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.setup.accounts.selection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import de.kuschku.quasseldroid.Keys
import de.kuschku.quasseldroid.ui.setup.SetupActivity
import de.kuschku.quasseldroid.util.helper.editCommit

class AccountSelectionActivity : SetupActivity() {
  companion object {
    const val REQUEST_CREATE_FIRST = 0
    const val REQUEST_CREATE_NEW = 1

    fun launch(context: Context) = context.startActivity(intent(context))
    fun intent(context: Context) = Intent(context, AccountSelectionActivity::class.java)
  }

  override val fragments = listOf(
    AccountSelectionSlide()
  )

  private lateinit var statusPreferences: SharedPreferences
  override fun onDone(data: Bundle) {
    statusPreferences.editCommit {
      putLong(Keys.Status.selectedAccount, data.getLong(Keys.Status.selectedAccount, -1))
      putBoolean(Keys.Status.reconnect, true)
    }
    setResult(Activity.RESULT_OK)
    finish()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    statusPreferences = this.getSharedPreferences(Keys.Status.NAME, Context.MODE_PRIVATE)
    val data = Bundle()
    val selectedAccount = statusPreferences.getLong(Keys.Status.selectedAccount, -1)
    data.putLong(Keys.Status.selectedAccount, selectedAccount)
    setInitData(data)
  }
}
